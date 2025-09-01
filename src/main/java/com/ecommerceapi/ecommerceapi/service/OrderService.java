package com.ecommerceapi.ecommerceapi.service;

import com.ecommerceapi.ecommerceapi.config.Log;
import com.ecommerceapi.ecommerceapi.dto.ProductListDTO;

import com.ecommerceapi.ecommerceapi.dto.UserDTO;
import com.ecommerceapi.ecommerceapi.entity.Order;
import com.ecommerceapi.ecommerceapi.entity.Orders;
import com.ecommerceapi.ecommerceapi.feignclient.BankAppClient;
import com.ecommerceapi.ecommerceapi.feignclient.FundTransferResponseDTO;
import com.ecommerceapi.ecommerceapi.feignclient.UserFundTransferDTO;
import com.ecommerceapi.ecommerceapi.repository.OrderRepository;
import com.ecommerceapi.ecommerceapi.validator.EcommerceValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    Logger logger = LoggerFactory.getLogger(OrderService.class);

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

    @Log
    @Transactional
    public void placeOrder(Long userId) {

        logger.debug("Placing order for user {}", userId);
        ProductListDTO productListDTO = cartService.findCartProductDetails(userId);
        logger.debug("Product list in the cart is {}", productListDTO);
        UserDTO user = userService.getUsersDetailsById(userId);
        UserFundTransferDTO userFundTransferDTO = getUserFundTransferDTO(user, productListDTO);

        logger.debug("Fund transfer started for user in Bank App {}", userFundTransferDTO.getUserName());

        FundTransferResponseDTO fundTransferResponseDTO =
                bankAppClient.transferFundsForEcommerceAccount(userFundTransferDTO, userId);

        logger.debug("Fund transfer response received from bank app for user {}", userFundTransferDTO.getUserName());
        logger.debug("Transfer response: {} " , fundTransferResponseDTO.toString());

        ecommerceValidator.validate(fundTransferResponseDTO.getAmount(), productListDTO.getBasketPrice());

        List<Order> orderList= getOrders(userId, fundTransferResponseDTO, productListDTO, user);

        Orders ordersDocument = new Orders();
        ordersDocument.setOrders(orderList);


        orderRepository.save(ordersDocument);
        cartService.cleanUpCartData(userId);
    }

    private static List<Order> getOrders(Long userId, FundTransferResponseDTO fundTransferResponseDTO, ProductListDTO productListDTO, UserDTO user) {
        String transactionId = fundTransferResponseDTO.getTransactionId();
        LocalDateTime orderDate = LocalDateTime.now();

        return productListDTO.getProducts().stream()
                .map(product -> new Order()
                                .setProductId(product.getProductId())
                                .setDescription(product.getDescription())
                                .setUnitPrice(product.getPrice())
                                .setUserId(userId)
                                .setOrderReference(transactionId)
                                .setOrderDate(orderDate)
                                .setProductName(product.getProductName())
                                .setTotalPrice(fundTransferResponseDTO.getAmount())
                                .setQuantity(product.getQuantity())
                                .setUserName(user.getFirstName() +" "+ user.getLastName())
                        ).toList();
    }

    private UserFundTransferDTO getUserFundTransferDTO(UserDTO user, ProductListDTO productListDTO) {
        UserFundTransferDTO userFundTransferDTO = new UserFundTransferDTO();
        userFundTransferDTO.setUserName(user.getFirstName() +" "+ user.getLastName());
        userFundTransferDTO.setToAccountName(merchantAccountNo);
        userFundTransferDTO.setAmount(productListDTO.getBasketPrice());
        return userFundTransferDTO;
    }
}
