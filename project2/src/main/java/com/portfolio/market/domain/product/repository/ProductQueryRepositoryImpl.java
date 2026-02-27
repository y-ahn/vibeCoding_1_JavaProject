package com.portfolio.market.domain.product.repository;

import com.portfolio.market.domain.product.dto.ProductSearchCondition;
import com.portfolio.market.domain.product.entity.Product;
import com.portfolio.market.domain.product.entity.ProductStatus;
import com.portfolio.market.domain.product.entity.QProduct;
import com.portfolio.market.domain.user.entity.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> search(ProductSearchCondition cond, Pageable pageable) {
        QProduct product = QProduct.product;
        QUser seller = QUser.user;

        // ✅ QueryDSL Fetch Join — seller N+1 방지
        List<Product> content = queryFactory
                .selectFrom(product)
                .join(product.seller, seller).fetchJoin()
                .where(
                    keywordContains(cond.getKeyword()),     // null이면 조건 제외
                    categoryEq(cond.getCategory()),         // null이면 조건 제외
                    priceBetween(cond.getMinPrice(), cond.getMaxPrice()),
                    statusEq(cond.getStatus())              // null이면 SOLD 제외
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.createdAt.desc())
                .fetch();

        // COUNT 쿼리 분리 (Fetch Join과 분리해야 정확한 카운트)
        Long total = queryFactory
                .select(product.count())
                .from(product)
                .where(
                    keywordContains(cond.getKeyword()),
                    categoryEq(cond.getCategory()),
                    priceBetween(cond.getMinPrice(), cond.getMaxPrice()),
                    statusEq(cond.getStatus())
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> total == null ? 0 : total);
    }

    // ── BooleanExpression (null 반환 시 해당 WHERE 절 자동 제외) ──

    private BooleanExpression keywordContains(String keyword) {
        // null 또는 빈 문자열이면 null 반환 → QueryDSL이 WHERE 절에서 무시
        return StringUtils.hasText(keyword)
                ? QProduct.product.title.containsIgnoreCase(keyword)
                : null;
    }

    private BooleanExpression categoryEq(
            com.portfolio.market.domain.product.entity.ProductCategory category) {
        return category != null ? QProduct.product.category.eq(category) : null;
    }

    private BooleanExpression priceBetween(Integer min, Integer max) {
        if (min != null && max != null) return QProduct.product.price.between(min, max);
        if (min != null) return QProduct.product.price.goe(min);
        if (max != null) return QProduct.product.price.loe(max);
        return null;
    }

    private BooleanExpression statusEq(ProductStatus status) {
        // status 파라미터가 없으면 기본값: SOLD 제외
        return status != null
                ? QProduct.product.status.eq(status)
                : QProduct.product.status.ne(ProductStatus.SOLD);
    }
}
