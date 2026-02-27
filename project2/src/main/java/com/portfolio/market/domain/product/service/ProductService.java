package com.portfolio.market.domain.product.service;

import com.portfolio.market.domain.product.dto.*;
import com.portfolio.market.domain.product.entity.*;
import com.portfolio.market.domain.product.repository.ProductQueryRepository;
import com.portfolio.market.domain.product.repository.ProductRepository;
import com.portfolio.market.domain.user.entity.User;
import com.portfolio.market.domain.user.repository.UserRepository;
import com.portfolio.market.global.exception.CustomException;
import com.portfolio.market.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductQueryRepository productQueryRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProductResponse create(Long sellerId, ProductCreateRequest req) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Product product = Product.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .price(req.getPrice())
                .category(req.getCategory())
                .status(ProductStatus.SELLING)
                .seller(seller)
                .build();

        return ProductResponse.from(productRepository.save(product));
    }

    // 검색 조건 기반 목록 조회 — QueryDSL 동적 쿼리
    public Page<ProductResponse> search(ProductSearchCondition condition, Pageable pageable) {
        return productQueryRepository.search(condition, pageable)
                .map(ProductResponse::from);
    }

    // 단건 조회 — Fetch Join으로 N+1 방지
    public ProductResponse getOne(Long productId) {
        Product product = productRepository.findByIdWithSeller(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        return ProductResponse.from(product);
    }

    // 내 상품 목록
    public Page<ProductResponse> getMyProducts(Long sellerId, Pageable pageable) {
        return productRepository.findBySellerIdWithSeller(sellerId, pageable)
                .map(ProductResponse::from);
    }

    @Transactional
    public ProductResponse update(Long sellerId, Long productId, ProductUpdateRequest req) {
        Product product = ownerCheck(sellerId, productId);
        product.update(req);
        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long sellerId, Long productId) {
        productRepository.delete(ownerCheck(sellerId, productId));
    }

    @Transactional
    public ProductResponse changeStatus(Long sellerId, Long productId, ProductStatus status) {
        Product product = ownerCheck(sellerId, productId);
        product.changeStatus(status);
        return ProductResponse.from(product);
    }

    private Product ownerCheck(Long userId, Long productId) {
        Product product = productRepository.findByIdWithSeller(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!product.isSeller(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        return product;
    }
}
