package com.portfolio.market.domain.trade.service;

import com.portfolio.market.domain.product.entity.Product;
import com.portfolio.market.domain.product.entity.ProductStatus;
import com.portfolio.market.domain.product.repository.ProductRepository;
import com.portfolio.market.domain.trade.dto.TradeResponse;
import com.portfolio.market.domain.trade.entity.Trade;
import com.portfolio.market.domain.trade.entity.TradeStatus;
import com.portfolio.market.domain.trade.repository.TradeRepository;
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
public class TradeService {

    private final TradeRepository tradeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // 구매 요청
    @Transactional
    public TradeResponse requestTrade(Long buyerId, Long productId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findByIdWithSeller(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 본인 상품 구매 불가
        if (product.isSeller(buyerId)) {
            throw new CustomException(ErrorCode.CANNOT_BUY_OWN_PRODUCT);
        }

        // 이미 판매된 상품 구매 불가
        if (product.getStatus() == ProductStatus.SOLD) {
            throw new CustomException(ErrorCode.PRODUCT_ALREADY_SOLD);
        }

        // 상품 상태 → 예약중으로 변경
        product.changeStatus(ProductStatus.RESERVED);

        Trade trade = Trade.builder()
                .buyer(buyer)
                .product(product)
                .tradePrice(product.getPrice())
                .status(TradeStatus.PENDING)
                .build();

        return TradeResponse.from(tradeRepository.save(trade));
    }

    // 거래 완료 확정 (판매자가 확정)
    @Transactional
    public TradeResponse completeTrade(Long sellerId, Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new CustomException(ErrorCode.TRADE_NOT_FOUND));

        if (!trade.getProduct().isSeller(sellerId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        trade.complete();
        trade.getProduct().changeStatus(ProductStatus.SOLD);
        return TradeResponse.from(trade);
    }

    // 거래 취소
    @Transactional
    public TradeResponse cancelTrade(Long userId, Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new CustomException(ErrorCode.TRADE_NOT_FOUND));

        boolean isBuyer = trade.getBuyer().getId().equals(userId);
        boolean isSeller = trade.getProduct().isSeller(userId);
        if (!isBuyer && !isSeller) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        trade.cancel();
        trade.getProduct().changeStatus(ProductStatus.SELLING);
        return TradeResponse.from(trade);
    }

    // 구매 내역 조회
    public Page<TradeResponse> getMyPurchases(Long buyerId, Pageable pageable) {
        return tradeRepository.findByBuyerIdWithDetails(buyerId, pageable)
                .map(TradeResponse::from);
    }

    // 판매 내역 조회
    public Page<TradeResponse> getMySales(Long sellerId, Pageable pageable) {
        return tradeRepository.findBySellerIdWithDetails(sellerId, pageable)
                .map(TradeResponse::from);
    }
}
