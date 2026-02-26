package com.portfolio.schedule.domain.schedule.controller;

import com.portfolio.schedule.domain.schedule.dto.ScheduleRequest;
import com.portfolio.schedule.domain.schedule.dto.ScheduleResponse;
import com.portfolio.schedule.domain.schedule.service.ScheduleService;
import com.portfolio.schedule.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    private Long userId(UserDetails ud) { return Long.parseLong(ud.getUsername()); }

    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleResponse>> create(
            @AuthenticationPrincipal UserDetails ud,
            @Valid @RequestBody ScheduleRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(scheduleService.create(userId(ud), req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ScheduleResponse>>> list(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10, sort = "startDate",
                             direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                scheduleService.getMySchedules(userId(ud), categoryId, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ScheduleResponse>> getOne(
            @AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(scheduleService.getOne(userId(ud), id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ScheduleResponse>> update(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long id,
            @Valid @RequestBody ScheduleRequest req) {
        return ResponseEntity.ok(ApiResponse.success(scheduleService.update(userId(ud), id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        scheduleService.delete(userId(ud), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<ScheduleResponse>> toggleComplete(
            @AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                scheduleService.toggleComplete(userId(ud), id)));
    }
}
