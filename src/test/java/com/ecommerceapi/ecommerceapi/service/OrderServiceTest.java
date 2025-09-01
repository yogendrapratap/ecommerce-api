package com.ecommerceapi.ecommerceapi.service;

import com.ecommerceapi.ecommerceapi.dto.ProductDTO;
import com.ecommerceapi.ecommerceapi.dto.ProductListDTO;
import com.ecommerceapi.ecommerceapi.dto.UserDTO;
import com.ecommerceapi.ecommerceapi.feignclient.BankAppClient;
import com.ecommerceapi.ecommerceapi.feignclient.FundTransferResponseDTO;
import com.ecommerceapi.ecommerceapi.repository.OrderRepository;
import com.ecommerceapi.ecommerceapi.validator.EcommerceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private UserService userService;

    @Mock
    private BankAppClient bankAppClient;

    @Mock
    private EcommerceValidator ecommerceValidator;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderService, "merchantAccountNo", 123456L);
    }

    @Test
    void testPlaceOrderSuccess() {
        Long userId = 1L;

        // Arrange: mock user
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");

        // Arrange: mock product list
        ProductDTO product1 = new ProductDTO().setProductId("101").setProductName("Phone")
                .setDescription("Smartphone").setPrice(new BigDecimal("10000.00"));
        ProductDTO product2 = new ProductDTO().setProductId("102").setProductName("Headphones")
                .setDescription("Wireless").setPrice(new BigDecimal("2000.00"));

        ProductListDTO productListDTO = new ProductListDTO();
        productListDTO.setProducts(List.of(product1, product2));
        productListDTO.setBasketPrice(new BigDecimal("12000.00"));

        FundTransferResponseDTO fundTransferResponse = new FundTransferResponseDTO();
        fundTransferResponse.setAmount(new BigDecimal("12000.00"));

        // Mocks
        when(cartService.findCartProductDetails(userId)).thenReturn(productListDTO);
        when(userService.getUsersDetailsById(userId)).thenReturn(userDTO);
        when(bankAppClient.transferFundsForEcommerceAccount(any(), eq(userId)))
                .thenReturn(fundTransferResponse);

        // Act
        orderService.placeOrder(userId);

        // Assert
        verify(cartService).findCartProductDetails(userId);
        verify(userService).getUsersDetailsById(userId);
        verify(bankAppClient).transferFundsForEcommerceAccount(any(), eq(userId));
        verify(ecommerceValidator).validate(new BigDecimal("12000.00"), new BigDecimal("12000.00"));
        verify(orderRepository).saveAll(anyList());
        verify(cartService).cleanUpCartData(userId);
    }
}

