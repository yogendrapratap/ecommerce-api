package com.ecommerceapi.ecommerceapi.service;

import com.ecommerceapi.ecommerceapi.dto.*;
import com.ecommerceapi.ecommerceapi.entity.Cart;
import com.ecommerceapi.ecommerceapi.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private ProductDTO productDTO;

    UserDTO userDTO;

    @BeforeEach
    void setUp() {
        cart = new Cart()
                .setProductId(1L)
                .setUserId(10L)
                .setQuantity(2);

        productDTO = new ProductDTO();
        productDTO.setId("1L");
        productDTO.setPrice(BigDecimal.valueOf(100));
        productDTO.setProductName("Test Product");

        userDTO = new UserDTO();


    }

    @Test
    void testFindCartProductDetails() {
        List<Cart> cartList = List.of(cart);
        when(cartRepository.findAllByUserId(10L)).thenReturn(cartList);

        ProductListDTO productListDTO = new ProductListDTO()
                .setProducts(List.of(productDTO));

        when(productService.searchProductListForCart(anyList())).thenReturn(productListDTO);
        //when(userService.getUsersDetailsById(anyLong())).thenReturn(userDTO);

        ProductListDTO result = cartService.findCartProductDetails(10L);

        assertEquals(BigDecimal.valueOf(0L), result.getBasketPrice());
        assertEquals(1, result.getProducts().size());
        assertEquals("Test Product", result.getProducts().get(0).getProductName());
        verify(cartRepository).findAllByUserId(10L);
        verify(productService).searchProductListForCart(List.of(1L));
    }

    @Test
    void testAddToCartWithNewItems() {
        Long userId = 1L;

        CartDTO cartDTO1 = new CartDTO(1L, userId, 2);
        CartDTO cartDTO2 = new CartDTO(2L, userId, 2);
        CartListDTO inputDTO = new CartListDTO().setCartDTO(List.of(cartDTO1, cartDTO2));

        when(cartRepository.findAllByUserId(userId)).thenReturn(List.of());

        // Mock saved entities
        Cart savedCart1 = new Cart().setProductId(1L).setUserId(userId).setQuantity(2);
        Cart savedCart2 = new Cart().setProductId(2L).setUserId(userId).setQuantity(2);
        List<Cart> savedEntities = List.of(savedCart1, savedCart2);

        when(cartRepository.saveAll(anyList())).thenReturn(savedEntities);

        // Mock DTO conversion
        List<CartDTO> resultDTOs = List.of(
                new CartDTO(1L, userId, 2),
                new CartDTO(2L, userId, 2)
        );
        // Let convertToDTOList run actual logic (if not overridden)
        // OR you can spy on CartService and return `resultDTOs` if mocking

        CartListDTO result = cartService.addToCart(inputDTO, userId);

        assertNotNull(result);
        assertEquals(2, result.getCartDTO().size());
        assertEquals(1L, result.getCartDTO().get(0).getProductId());
        assertEquals(2L, result.getCartDTO().get(1).getProductId());

        verify(cartRepository).saveAll(anyList());
    }

    @Test
    void testAddToCartWithNewItemsWithExistingList() {
        Long userId = 1L;

        // Input DTOs
        CartDTO cartDTO1 = new CartDTO(1L, userId, 2);
        CartDTO cartDTO2 = new CartDTO(2L, userId, 2);
        CartListDTO inputDTO = new CartListDTO().setCartDTO(List.of(cartDTO1, cartDTO2));

        Cart cart1 = new Cart();
        cart1.setUserId(userId);
        cart1.setQuantity(2);
        cart1.setProductId(1L);
        cart1.setId(1L);
        Cart cart2 = new Cart();
        cart2.setUserId(userId);
        cart2.setQuantity(2);
        cart2.setProductId(1L);
        cart2.setId(1L);
        List<Cart> savedEntities1 = List.of(cart1, cart2);

        // No existing cart items
        when(cartRepository.findAllByUserId(userId)).thenReturn(savedEntities1);

        // Mock saved entities
        Cart savedCart1 = new Cart().setProductId(1L).setUserId(userId).setQuantity(2);
        Cart savedCart2 = new Cart().setProductId(2L).setUserId(userId).setQuantity(2);
        List<Cart> savedEntities = List.of(savedCart1, savedCart2);

        when(cartRepository.saveAll(anyList())).thenReturn(savedEntities);

        // Mock DTO conversion
        List<CartDTO> resultDTOs = List.of(
                new CartDTO(1L, userId, 2),
                new CartDTO(2L, userId, 2)
        );
        // Let convertToDTOList run actual logic (if not overridden)
        // OR you can spy on CartService and return `resultDTOs` if mocking

        CartListDTO result = cartService.addToCart(inputDTO, userId);

        assertNotNull(result);
        assertEquals(2, result.getCartDTO().size());
        assertEquals(1L, result.getCartDTO().get(0).getProductId());
        assertEquals(2L, result.getCartDTO().get(1).getProductId());

        verify(cartRepository).saveAll(anyList());
    }


    @Test
    void testAssignsQuantityFromCartToProductDTO() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // Arrange your test data
        Cart cart = new Cart();
        cart.setProductId(1L);
        cart.setQuantity(5);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(1L); // String as per your matching logic
        productDTO.setProductName("Test");
        productDTO.setQuantity(0); // Should be updated

        ProductListDTO productListDTO = new ProductListDTO();
        productListDTO.setProducts(List.of(productDTO));

        CartDTO cartDTO = new CartDTO(1L, 1L, 5);
        List<CartDTO> cartDTOs = List.of(cartDTO);

        // Act
        // Option 1: Call the public method as in your application flow
        // productListDTO = cartService.findCartProductDetails(userId);

        // Option 2: (Less preferred) Use reflection to access the private method
        Method method = CartService.class.getDeclaredMethod(
            "assignQuantityFromCartToProductDTO", ProductListDTO.class, List.class
        );
        method.setAccessible(true);
        method.invoke(cartService, productListDTO, cartDTOs);

        // Assert that the quantity was assigned
        assertEquals(5, productListDTO.getProducts().get(0).getQuantity());
    }


    @Test
    void testConvertToDTOList() {
        List<Cart> carts = List.of(cart);
        List<CartDTO> dtos = cartService.convertToDTOList(carts);
        assertEquals(1, dtos.size());
        assertEquals(1L, dtos.get(0).getProductId());
        assertEquals(2, dtos.get(0).getQuantity());
    }

    @Test
    void testToEntityList() {
        CartDTO cartDTO = new CartDTO(1L, 10L, 3);
        List<CartDTO> dtoList = List.of(cartDTO);

        List<Cart> entityList = cartService.toEntityList(dtoList, 1L);

        assertEquals(1, entityList.size());
        assertEquals(1L, entityList.get(0).getProductId());
        assertEquals(3, entityList.get(0).getQuantity());
    }

    @Test
    void testConvertToDTO() {
        CartDTO result = cartService.convertToDTO(cart);
        assertEquals(1L, result.getProductId());
        assertEquals(2, result.getQuantity());
    }

    @Test
    void testConvertToEntity() {
        CartDTO cartDTO = new CartDTO(1L, 10L, 2);
        Cart result = cartService.convertToEntity(cartDTO);
        assertEquals(1L, result.getProductId());
        assertEquals(2, result.getQuantity());
    }

    @Test
    void testConvertToDTO_NullInput() {
        assertNull(cartService.convertToDTO(null));
    }

    @Test
    void testConvertToEntity_NullInput() {
        assertNull(cartService.convertToEntity(null));
    }

    @Test
    void testCleanUpCartData() {
        Long userId = 10L;
        cartService.cleanUpCartData(userId);
        verify(cartRepository).deleteAllByUserId(userId);
    }

    @Test
    void testSingleMatch() throws Exception {
        ProductDTO product = new ProductDTO();
        product.setProductId(1L);
        product.setPrice(BigDecimal.valueOf(50));

        CartDTO cart = new CartDTO(1L, 1L, 2);

        ProductListDTO productListDTO = new ProductListDTO();
        productListDTO.setProducts(List.of(product));
        List<CartDTO> cartDTOs = List.of(cart);

        BigDecimal result = invokeGetTotalOfBasket(productListDTO, cartDTOs);
        assertEquals(BigDecimal.valueOf(100), result);
    }

    @Test
    void testMultipleMatches() throws Exception {
        ProductDTO prod1 = new ProductDTO();
        prod1.setProductId(11L);
        prod1.setPrice(BigDecimal.valueOf(10));
        ProductDTO prod2 = new ProductDTO();
        prod2.setProductId(22L);
        prod2.setPrice(BigDecimal.valueOf(20));

        CartDTO cart1 = new CartDTO(11L, 1L, 3);
        CartDTO cart2 = new CartDTO(22L, 1L, 1);

        ProductListDTO productListDTO = new ProductListDTO();
        productListDTO.setProducts(List.of(prod1, prod2));
        List<CartDTO> cartDTOs = List.of(cart1, cart2);

        BigDecimal result = invokeGetTotalOfBasket(productListDTO, cartDTOs);
        // (10*3)+(20*1) = 30+20 = 50
        assertEquals(BigDecimal.valueOf(50), result);
    }

    @Test
    void testNoMatches() throws Exception {
        ProductDTO product = new ProductDTO();
        product.setProductId(100L);
        product.setPrice(BigDecimal.valueOf(100));

        CartDTO cart = new CartDTO(999L, 1L, 5);

        ProductListDTO productListDTO = new ProductListDTO();
        productListDTO.setProducts(List.of(product));
        List<CartDTO> cartDTOs = List.of(cart);

        BigDecimal result = invokeGetTotalOfBasket(productListDTO, cartDTOs);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testEmptyInputs() throws Exception {
        ProductListDTO productListDTO = new ProductListDTO();
        productListDTO.setProducts(Collections.emptyList());
        List<CartDTO> cartDTOs = Collections.emptyList();

        BigDecimal result = invokeGetTotalOfBasket(productListDTO, cartDTOs);
        assertEquals(BigDecimal.ZERO, result);
    }


    private static BigDecimal invokeGetTotalOfBasket(ProductListDTO products, List<CartDTO> carts) throws Exception {
        Method m = CartService.class.getDeclaredMethod(
                "getTotalOfBasket", ProductListDTO.class, List.class);
        m.setAccessible(true);
        return (BigDecimal) m.invoke(null, products, carts);
    }
}
