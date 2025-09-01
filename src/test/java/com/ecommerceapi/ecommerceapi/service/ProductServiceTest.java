package com.ecommerceapi.ecommerceapi.service;

import com.ecommerceapi.ecommerceapi.dto.ProductDTO;
import com.ecommerceapi.ecommerceapi.dto.ProductListDTO;
import com.ecommerceapi.ecommerceapi.entity.Product;
import com.ecommerceapi.ecommerceapi.exception.ECommerceAPIValidationException;
import com.ecommerceapi.ecommerceapi.repository.ProductRepository;
import com.ecommerceapi.ecommerceapi.validator.EcommerceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;

    @Mock
    private EcommerceValidator ecommerceValidator;

    @BeforeEach
    void setup() {
        product = new Product();
        product.setId(1L);
        product.setProductName("iPhone");

        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setProductName("iPhone");
    }

    @Test
    void testSearchByProductName() {
        when(productRepository.findByProductNameLikeIgnoreCase("iphone"))
                .thenReturn(List.of(product));
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        ProductListDTO result = productService.search("iphone");

        assertEquals(1, result.getProducts().size());
        assertEquals("iPhone", result.getProducts().get(0).getProductName());
        verify(productRepository).findByProductNameLikeIgnoreCase("iphone");
    }

    @Test
    void testSearchProductsPagination() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").ascending());
        Page<Product> mockPage = new PageImpl<>(List.of(product));

        when(productRepository.findAll(pageable)).thenReturn(mockPage);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        ProductListDTO result = productService.searchProducts(0, 5);

        assertEquals(1, result.getProducts().size());
        assertEquals("iPhone", result.getProducts().get(0).getProductName());
        verify(productRepository).findAll(pageable);
    }

    @Test
    void testSearchProductListForCart() {
        List<Long> productIds = List.of(1L, 2L);
        when(productRepository.findAllByProductIdIn(productIds)).thenReturn(List.of(product));
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        ProductListDTO result = productService.searchProductListForCart(productIds);

        assertEquals(1, result.getProducts().size());
        assertEquals("iPhone", result.getProducts().get(0).getProductName());
        verify(productRepository).findAllByProductIdIn(productIds);
    }

    @Test
    void testSearchProduct_ReturnsMappedDTO() {

        // Given
        Long productId = 1L;

        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("Phone");
        product.setPrice(BigDecimal.valueOf(500));

        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId("1");
        productDTO.setProductName("Phone");
        productDTO.setPrice(BigDecimal.valueOf(500));

        // When
        when(productRepository.findAllByProductId(productId)).thenReturn(product);
        doNothing().when(ecommerceValidator).validateProduct(product);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        // Act
        ProductDTO result = productService.searchProduct(productId);

        // Then
        assertNotNull(result);
        assertEquals("1", result.getProductId());
        assertEquals("Phone", result.getProductName());
        assertEquals(BigDecimal.valueOf(500), result.getPrice());

        verify(productRepository).findAllByProductId(productId);
        verify(ecommerceValidator).validateProduct(product);
        verify(modelMapper).map(product, ProductDTO.class);
    }

    @Test
    void testSearchProduct_ValidationFails_ThrowsException() {
        // Given
        Long productId = 2L;
        Product product = new Product();
        product.setProductId(productId);

        when(productRepository.findAllByProductId(productId)).thenReturn(product);
        doThrow(new ECommerceAPIValidationException("Product not found"))
                .when(ecommerceValidator).validateProduct(product);

        // Act & Assert
        assertThrows(ECommerceAPIValidationException.class, () -> {
            productService.searchProduct(productId);
        });

        verify(productRepository).findAllByProductId(productId);
        verify(ecommerceValidator).validateProduct(product);
        verifyNoInteractions(modelMapper);
    }
}
