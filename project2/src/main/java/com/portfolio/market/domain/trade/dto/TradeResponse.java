package com.portfolio.market.domain.trade.dto;

import com.portfolio.market.domain.trade.entity.Trade;
import com.portfolio.market.domain.trade.entity.TradeStatus;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class TradeResponse {
    private Long id;
    private Long productId;
    private String productTitle;
    private Integer tradePrice;
    private String buyerNickname;
    private String sellerNickname;
    private TradeStatus status;
    private LocalDateTime createdAt;

    public static TradeResponse from(Trade trade) {
        TradeResponse res = new TradeResponse();
        res.id = trade.getId();
        res.productId = trade.getProduct().getId();
        res.productTitle = trade.getProduct().getTitle();
        res.tradePrice = trade.getTradePrice();
        res.buyerNickname = trade.getBuyer().getNickname();
        res.sellerNickname = trade.getProduct().getSeller().getNickname();
        res.status = trade.getStatus();
        res.createdAt = trade.getCreatedAt();
        return res;
    }
}
