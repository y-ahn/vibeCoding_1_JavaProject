package com.portfolio.schedule.service;

import com.portfolio.schedule.domain.category.repository.CategoryRepository;
import com.portfolio.schedule.domain.schedule.dto.*;
import com.portfolio.schedule.domain.schedule.entity.Schedule;
import com.portfolio.schedule.domain.schedule.repository.ScheduleRepository;
import com.portfolio.schedule.domain.schedule.service.ScheduleService;
import com.portfolio.schedule.domain.user.entity.User;
import com.portfolio.schedule.domain.user.repository.UserRepository;
import com.portfolio.schedule.global.exception.CustomException;
import com.portfolio.schedule.global.exception.ErrorCode;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock private ScheduleRepository scheduleRepository;
    @Mock private UserRepository userRepository;
    @Mock private CategoryRepository categoryRepository;
    @InjectMocks private ScheduleService scheduleService;

    private static void set(Object obj, String fieldName, Object value) throws Exception {
        Field f = findField(obj.getClass(), fieldName);
        f.setAccessible(true);
        f.set(obj, value);
    }
    private static Field findField(Class<?> clazz, String name) throws NoSuchFieldException {
        try { return clazz.getDeclaredField(name); }
        catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) return findField(clazz.getSuperclass(), name);
            throw e;
        }
    }

    @Test
    @DisplayName("일정 생성 성공")
    void createSchedule_success() throws Exception {
        Long userId = 1L;
        User user = User.builder().email("test@test.com").password("pw").name("테스터").build();
        set(user, "id", userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(scheduleRepository.save(any())).willAnswer(i -> i.getArgument(0));

        ScheduleRequest req = new ScheduleRequest();
        set(req, "title", "테스트 일정");
        set(req, "startDate", LocalDate.now());

        ScheduleResponse res = scheduleService.create(userId, req);

        assertThat(res.getTitle()).isEqualTo("테스트 일정");
        verify(scheduleRepository).save(any());
    }

    @Test
    @DisplayName("일정 삭제 실패 — 다른 유저의 일정은 FORBIDDEN")
    void deleteSchedule_forbidden() throws Exception {
        User owner = User.builder().email("owner@test.com").password("pw").name("소유자").build();
        set(owner, "id", 1L);
        Schedule schedule = Schedule.builder().user(owner).title("내 일정")
            .startDate(LocalDate.now()).build();
        set(schedule, "id", 1L);

        given(scheduleRepository.findById(1L)).willReturn(Optional.of(schedule));

        assertThatThrownBy(() -> scheduleService.delete(2L, 1L))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN);
    }

    @Test
    @DisplayName("존재하지 않는 일정 조회 — SCHEDULE_NOT_FOUND")
    void getOne_notFound() {
        given(scheduleRepository.findByIdAndUserId(99L, 1L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> scheduleService.getOne(1L, 99L))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SCHEDULE_NOT_FOUND);
    }
}
