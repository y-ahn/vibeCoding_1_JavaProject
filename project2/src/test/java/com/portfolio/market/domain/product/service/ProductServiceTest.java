package com.portfolio.market.domain.product.service;

import com.portfolio.market.domain.product.dto.ProductCreateRequest;
import com.portfolio.market.domain.product.dto.ProductSearchCondition;
import com.portfolio.market.domain.product.entity.*;
import com.portfolio.market.domain.product.repository.ProductQueryRepository;
import com.portfolio.market.domain.product.repository.ProductRepository;
import com.portfolio.market.domain.user.entity.User;
import com.portfolio.market.domain.user.repository.UserRepository;
import com.portfolio.market.global.exception.CustomException;
import com.portfolio.market.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks ProductService productService;
    @Mock ProductRepository productRepository;
    @Mock ProductQueryRepository productQueryRepository;
    @Mock UserRepository userRepository;

    @Test
    @DisplayName("상품 단건 조회 — 정상")
    void getOne_success() {
        User seller = User.builder().id(1L).email("seller@test.com")
                .password("pw").nickname("판매자").build();
        Product product = Product.builder().id(1L).title("맥북 팝니다")
                .description("상태 좋음").price(1_500_000)
                .status(ProductStatus.SELLING)
                .category(ProductCategory.ELECTRONICS)
                .seller(seller).build();

        given(productRepository.findByIdWithSeller(1L)).willReturn(Optional.of(product));

        var result = productService.getOne(1L);

        assertThat(result.getTitle()).isEqualTo("맥북 팝니다");
        assertThat(result.getPrice()).isEqualTo(1_500_000);
        assertThat(result.getSellerNickname()).isEqualTo("판매자");
    }

    @Test
    @DisplayName("상품 단건 조회 — 존재하지 않는 상품")
    void getOne_notFound() {
        given(productRepository.findByIdWithSeller(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getOne(999L))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getErrorCode())
                        .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Test
    @DisplayName("상품 삭제 — 본인이 아닌 경우 FORBIDDEN")
    void delete_forbidden() {
        User seller = User.builder().id(1L).email("seller@test.com")
                .password("pw").nickname("판매자").build();
        Product product = Product.builder().id(1L).title("맥북").description("좋음")
                .price(100_000).status(ProductStatus.SELLING)
                .category(ProductCategory.ELECTRONICS).seller(seller).build();

        given(productRepository.findByIdWithSeller(1L)).willReturn(Optional.of(product));

        // userId=2L (다른 유저)로 삭제 시도
        assertThatThrownBy(() -> productService.delete(2L, 1L))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getErrorCode())
                        .isEqualTo(ErrorCode.FORBIDDEN));
    }

    @Test
    @DisplayName("상품 검색 — 조건 없이 전체 조회")
    void search_noCondition() {
        User seller = User.builder().id(1L).email("s@test.com")
                .password("pw").nickname("판매자").build();
        Product p = Product.builder().id(1L).title("아이폰").description("새상품")
                .price(500_000).status(ProductStatus.SELLING)
                .category(ProductCategory.ELECTRONICS).seller(seller).build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(p), pageable, 1);

        given(productQueryRepository.search(any(), eq(pageable))).willReturn(page);

        var result = productService.search(new ProductSearchCondition(), pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("아이폰");
    }
}
