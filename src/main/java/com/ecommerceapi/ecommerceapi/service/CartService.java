package com.ecommerceapi.ecommerceapi.service;

import com.ecommerceapi.ecommerceapi.config.Log;
import com.ecommerceapi.ecommerceapi.dto.CartDTO;
import com.ecommerceapi.ecommerceapi.dto.CartListDTO;
import com.ecommerceapi.ecommerceapi.dto.ProductDTO;
import com.ecommerceapi.ecommerceapi.dto.ProductListDTO;
import com.ecommerceapi.ecommerceapi.entity.Cart;
import com.ecommerceapi.ecommerceapi.exception.ECommerceAPIValidationException;
import com.ecommerceapi.ecommerceapi.repository.CartRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @Log
    public ProductListDTO findCartProductDetails(Long userId) {
        List<Cart> carts = cartRepository.findAllByUserId(userId);

        List<CartDTO> cartDTOs = convertToDTOList(carts);

        List<Long> productIds = cartDTOs.stream()
                        .map(CartDTO::getProductId)
                                .toList();
        ProductListDTO productListDTO = productService.searchProductListForCart(productIds);

        BigDecimal total = getTotalOfBasket(productListDTO, cartDTOs);

         assignQuantityFromCartToProductDTO(productListDTO, cartDTOs);

        productListDTO.setBasketPrice(total);

        return productListDTO;
    }

    private void assignQuantityFromCartToProductDTO(ProductListDTO productListDTO, List<CartDTO> cartDTOs) {

        productListDTO.getProducts().forEach(productDTO -> {
            CartDTO matchCartDTO = cartDTOs.stream()
                    .filter(cartDTO -> String.valueOf(cartDTO.getProductId())
                            .equalsIgnoreCase(productDTO.getProductId().toString()))
                    .findFirst()
                    .orElse(null);

            int quantity = (matchCartDTO != null) ? matchCartDTO.getQuantity() : 0;
            productDTO.setQuantity(quantity);
        });
    }


    public CartListDTO addToCart(CartListDTO cartListDTO, Long userId) {
        //List<Cart> existingCart = cartRepository.findAllByUserId(userId);

        userService.getUsersDetailsById( userId);

        cartListDTO.getCartDTO().stream()
                .map(CartDTO::getProductId)
                .forEach(productId -> productService.searchProduct(productId));

        List<Cart> existingCart = new ArrayList<>(cartRepository.findAllByUserId(userId));

        List<CartDTO> toAddList = new java.util.ArrayList<>(cartListDTO.getCartDTO());
        List<CartDTO> finalListToAdd = new java.util.ArrayList<>();

        for (CartDTO newItem : toAddList) {
            boolean matched = false;
            for (Cart existing : existingCart) {
                if (existing.getProductId().equals(newItem.getProductId())) {
                    existing.setQuantity(existing.getQuantity() + newItem.getQuantity());
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                finalListToAdd.add(newItem);
            }
        }

        List<Cart> newCartList = toEntityList(finalListToAdd, userId);
        existingCart.addAll(newCartList);

        List<Cart> saved = cartRepository.saveAll(existingCart);
        List<CartDTO> cartDTOList = convertToDTOList(saved);

        return new CartListDTO().setCartDTO(cartDTOList);
    }

    public List<CartDTO> convertToDTOList(List<Cart> cartList) {
        return cartList.stream()
                .map(cart -> new CartDTO(
                        cart.getProductId(),
                        cart.getUserId(),
                        cart.getQuantity()))
                .collect(Collectors.toList());
    }

    public List<Cart> toEntityList(List<CartDTO> dtoList, Long userId) {
        return dtoList.stream().map(dto -> new Cart()
                .setProductId(dto.getProductId())
                .setUserId(userId)
                .setQuantity(dto.getQuantity())
                .setId(randomLongNumber())
        ).collect(Collectors.toList());
    }

    public CartDTO convertToDTO(Cart cart) {
        if (cart == null) return null;
        return new CartDTO(cart.getProductId(), cart.getUserId(), cart.getQuantity());
    }

    public Cart convertToEntity(CartDTO cartDTO) {
        if (cartDTO == null) return null;
        return new Cart()
                .setProductId(cartDTO.getProductId())
                .setUserId(cartDTO.getUserId())
                .setQuantity(cartDTO.getQuantity());
    }

    private static BigDecimal getTotalOfBasket(ProductListDTO productListDTO, List<CartDTO> cartDTOs) {
        return productListDTO.getProducts().stream()
                .flatMap(productDTO -> cartDTOs.stream()
                        .filter(cartDTO -> String.valueOf(cartDTO.getProductId())
                                .equalsIgnoreCase(productDTO.getProductId().toString()))
                        .map(cartDTO -> productDTO.getPrice().multiply(
                                new BigDecimal(cartDTO.getQuantity()))))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Long randomLongNumber(){
        Random random = new Random();
        long randomLong = random.nextLong();
        return Math.abs(randomLong);
    }


    @Log
    @Transactional
    public void cleanUpCartData(Long userId) {
        cartRepository.deleteAllByUserId(userId);
    }
}
