package com.portfolio.market.domain.product.repository;

import com.portfolio.market.domain.product.dto.ProductSearchCondition;
import com.portfolio.market.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQueryRepository {
    Page<Product> search(ProductSearchCondition condition, Pageable pageable);
}
