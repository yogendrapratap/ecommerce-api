package com.ecommerceapi.ecommerceapi.service;

import com.ecommerceapi.ecommerceapi.config.Log;
import com.ecommerceapi.ecommerceapi.dto.ProductDTO;
import com.ecommerceapi.ecommerceapi.dto.ProductListDTO;
import com.ecommerceapi.ecommerceapi.entity.Product;
import com.ecommerceapi.ecommerceapi.repository.ProductRepository;
import com.ecommerceapi.ecommerceapi.validator.EcommerceValidator;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final EcommerceValidator ecommerceValidator;

    public ProductService(ProductRepository productRepository, ModelMapper modelMapper, EcommerceValidator ecommerceValidator) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.ecommerceValidator = ecommerceValidator;
    }

    @Log
    public ProductListDTO search(String productName) {
        List<Product> products = productRepository.findByProductNameLikeIgnoreCase(productName);
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        return new ProductListDTO().setProducts(productDTOs);

    }

    @Log
    public ProductListDTO searchProducts(Integer pageNumber, Integer pageSize) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        List<Product> products = productRepository.findAll(pageable).getContent();
        List<ProductDTO> productDtos = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(toList());
        return new ProductListDTO().setProducts(productDtos);
    }

    @Log
    public ProductListDTO searchProductListForCart(List<Long> productIds) {
        List<Product> products = productRepository.findAllByProductIdIn(productIds);
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductListDTO productListDTO = new ProductListDTO();
        productListDTO.setProducts(productDTOs);
        return productListDTO;
    }

    @Log
    public ProductDTO searchProduct(Long productId) {
        Product products = productRepository.findAllByProductId(productId);
        ecommerceValidator.validateProduct(products);

        return modelMapper.map(products, ProductDTO.class);
    }


}
