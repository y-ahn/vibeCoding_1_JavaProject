package com.portfolio.feed.domain.feed.dto;
import lombok.*;
@Getter @AllArgsConstructor
public class RankingResponse {
    private Long postId;
    private Double score;
    private int rank;
}
