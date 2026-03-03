package com.portfolio.commerce.domain.product.service;

import com.portfolio.commerce.domain.product.dto.*;
import com.portfolio.commerce.domain.product.entity.*;
import com.portfolio.commerce.domain.product.repository.ProductRepository;
import com.portfolio.commerce.global.exception.CustomException;
import com.portfolio.commerce.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse create(ProductCreateRequest req) {
        Product product = Product.builder()
                .name(req.getName()).description(req.getDescription())
                .price(req.getPrice()).stock(req.getStock())
                .status(ProductStatus.ON_SALE).build();
        return ProductResponse.from(productRepository.save(product));
    }

    public Page<ProductResponse> getProducts(Pageable pageable) {
        return productRepository.findByStatusNot(ProductStatus.HIDDEN, pageable)
                .map(ProductResponse::from);
    }

    public ProductResponse getOne(Long id) {
        return ProductResponse.from(productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)));
    }
}
