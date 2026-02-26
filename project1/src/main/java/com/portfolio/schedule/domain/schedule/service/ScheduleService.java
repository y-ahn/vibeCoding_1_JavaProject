package com.portfolio.schedule.domain.schedule.service;

import com.portfolio.schedule.domain.category.entity.Category;
import com.portfolio.schedule.domain.category.repository.CategoryRepository;
import com.portfolio.schedule.domain.schedule.dto.ScheduleRequest;
import com.portfolio.schedule.domain.schedule.dto.ScheduleResponse;
import com.portfolio.schedule.domain.schedule.entity.Schedule;
import com.portfolio.schedule.domain.schedule.repository.ScheduleRepository;
import com.portfolio.schedule.domain.user.entity.User;
import com.portfolio.schedule.domain.user.repository.UserRepository;
import com.portfolio.schedule.global.exception.CustomException;
import com.portfolio.schedule.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ScheduleResponse create(Long userId, ScheduleRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Category category = null;
        if (req.getCategoryId() != null) {
            category = categoryRepository.findByIdAndUserId(req.getCategoryId(), userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        Schedule schedule = Schedule.builder()
                .user(user).category(category)
                .title(req.getTitle()).description(req.getDescription())
                .startDate(req.getStartDate()).endDate(req.getEndDate())
                .build();

        return ScheduleResponse.from(scheduleRepository.save(schedule));
    }

    public Page<ScheduleResponse> getMySchedules(Long userId, Long categoryId, Pageable pageable) {
        Page<Schedule> page = categoryId != null
                ? scheduleRepository.findByUserIdAndCategoryId(userId, categoryId, pageable)
                : scheduleRepository.findByUserId(userId, pageable);
        return page.map(ScheduleResponse::from);
    }

    public ScheduleResponse getOne(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findByIdAndUserId(scheduleId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));
        return ScheduleResponse.from(schedule);
    }

    @Transactional
    public ScheduleResponse update(Long userId, Long scheduleId, ScheduleRequest req) {
        Schedule schedule = ownerCheck(userId, scheduleId);
        schedule.update(req);
        return ScheduleResponse.from(schedule);
    }

    @Transactional
    public void delete(Long userId, Long scheduleId) {
        scheduleRepository.delete(ownerCheck(userId, scheduleId));
    }

    @Transactional
    public ScheduleResponse toggleComplete(Long userId, Long scheduleId) {
        Schedule schedule = ownerCheck(userId, scheduleId);
        schedule.toggleCompleted();
        return ScheduleResponse.from(schedule);
    }

    private Schedule ownerCheck(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));
        if (!schedule.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        return schedule;
    }
}
