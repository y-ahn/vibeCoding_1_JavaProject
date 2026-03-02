package com.portfolio.feed.domain.post.dto;

import lombok.*;
import java.util.List;

// Cursor 기반 페이징 응답
// hasNext: 다음 페이지 존재 여부 (클라이언트가 무한스크롤 계속할지 판단)
// nextCursor: 다음 요청 시 사용할 커서 값 (마지막 항목의 ID)
@Getter
@AllArgsConstructor
public class CursorResult<T> {
    private List<T> content;
    private Long nextCursor;   // null이면 마지막 페이지
    private boolean hasNext;

    public static <T> CursorResult<T> of(List<T> content, Long nextCursor) {
        return new CursorResult<>(content, nextCursor, nextCursor != null);
    }
}
