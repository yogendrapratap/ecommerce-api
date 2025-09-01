package com.ecommerceapi.ecommerceapi.service;

import com.ecommerceapi.ecommerceapi.config.Log;
import com.ecommerceapi.ecommerceapi.document.Delivery;
import com.ecommerceapi.ecommerceapi.document.Product;
import com.ecommerceapi.ecommerceapi.dto.ProductListDTO;
import com.ecommerceapi.ecommerceapi.dto.UserDTO;
import com.ecommerceapi.ecommerceapi.entity.Order;
import com.ecommerceapi.ecommerceapi.entity.Orders;
import com.ecommerceapi.ecommerceapi.feignclient.BankAppClient;
import com.ecommerceapi.ecommerceapi.feignclient.FundTransferResponseDTO;
import com.ecommerceapi.ecommerceapi.feignclient.UserFundTransferDTO;
import com.ecommerceapi.ecommerceapi.repository.OrderRepository;
import com.ecommerceapi.ecommerceapi.validator.EcommerceValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private BankAppClient bankAppClient;

    @Value("${merchant.account.no}")
    private Long merchantAccountNo;

    @Autowired
    private EcommerceValidator ecommerceValidator;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    /**
     * Places an order for the given user.
     * @param userId the user ID
     */
    @Log
    @Transactional
    public void placeOrder(Long userId) throws ExecutionException, JsonProcessingException, InterruptedException {
        logger.debug("Placing order for user {}", userId);
        final ProductListDTO productListDTO = cartService.findCartProductDetails(userId);
        logger.debug("Product list in the cart is {}", productListDTO);
        final UserDTO user = userService.getUsersDetailsById(userId);
        final UserFundTransferDTO userFundTransferDTO = buildUserFundTransferDTO(user, productListDTO);

        logger.debug("Fund transfer started for user in Bank App {}", userFundTransferDTO.getUserName());
        final FundTransferResponseDTO fundTransferResponseDTO = bankAppClient.transferFundsForEcommerceAccount(userFundTransferDTO, userId);
        logger.debug("Fund transfer response received from bank app for user {}", userFundTransferDTO.getUserName());
        logger.debug("Transfer response: {}", fundTransferResponseDTO);

        ecommerceValidator.validate(fundTransferResponseDTO.getAmount(), productListDTO.getBasketPrice());

        final List<Order> orderList = buildOrders(userId, fundTransferResponseDTO, productListDTO, user);
        Orders ordersDocument = new Orders();
        ordersDocument.setOrders(orderList);
        ordersDocument.setStatus("PENDING");
        ordersDocument.setOrderNumber(getCurrentEpochMillis());
        ordersDocument = orderRepository.save(ordersDocument);
        cartService.cleanUpCartData(userId);

        final Delivery delivery = buildDelivery(ordersDocument.getId(), productListDTO, user);
        final CompletableFuture<SendResult<String, String>> future = kafkaProducerService.sendResultAsync(delivery);
        final boolean isSuccess = waitForKafkaResult(future);

        ordersDocument = orderRepository.findByOrderNumber(ordersDocument.getOrderNumber());
        ordersDocument.setStatus(isSuccess ? "CONFIRMED" : "SHIPMENT_FAILED");
        orderRepository.save(ordersDocument);
    }

    /**
     * Builds the list of orders for the user and products.
     */
    private static List<Order> buildOrders(Long userId, FundTransferResponseDTO fundTransferResponseDTO, ProductListDTO productListDTO, UserDTO user) {
        final String transactionId = fundTransferResponseDTO.getTransactionId();
        final LocalDateTime orderDate = LocalDateTime.now();
        final String userName = user.getFirstName() + " " + user.getLastName();
        return productListDTO.getProducts().stream()
                .map(product -> new Order()
                        .setProductId(product.getProductId().toString())
                        .setDescription(product.getDescription())
                        .setUnitPrice(product.getPrice())
                        .setUserId(userId)
                        .setOrderReference(transactionId)
                        .setOrderDate(orderDate)
                        .setProductName(product.getProductName())
                        .setTotalPrice(fundTransferResponseDTO.getAmount())
                        .setQuantity(product.getQuantity())
                        .setUserName(userName)
                ).toList();
    }

    private UserFundTransferDTO buildUserFundTransferDTO(UserDTO user, ProductListDTO productListDTO) {
        UserFundTransferDTO dto = new UserFundTransferDTO();
        dto.setUserName(user.getFirstName() + " " + user.getLastName());
        dto.setToAccountName(merchantAccountNo);
        dto.setAmount(productListDTO.getBasketPrice());
        return dto;
    }

    private Delivery buildDelivery(String orderId, ProductListDTO productListDTO, UserDTO user) {
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setProducts(productListDTO.getProducts().stream().map(productDTO -> {
            Product product = new Product();
            product.setProductId(productDTO.getProductId());
            product.setProductName(productDTO.getProductName());
            product.setQuantity(productDTO.getQuantity());
            product.setDeliveryDate(getRandomDeliveryDate());
            return product;
        }).toList());
        delivery.setDeliveryStatus("WAITING_FOR_DISPATCH");
        delivery.setDeliveryAddress(user.getAddress());
        return delivery;
    }

    private boolean waitForKafkaResult(CompletableFuture<SendResult<String, String>> future) {
        try {
            future.get();
            return true;
        } catch (Exception e) {
            logger.error("Kafka send failed: {}", e.getMessage(), e);
            return false;
        }
    }


    private long getCurrentEpochMillis() {
        return LocalDate.now()
                .atStartOfDay()
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    private LocalDate getRandomDeliveryDate() {
        int daysToAdd = ThreadLocalRandom.current().nextInt(10, 16);
        return LocalDate.now().plusDays(daysToAdd);
    }
}
