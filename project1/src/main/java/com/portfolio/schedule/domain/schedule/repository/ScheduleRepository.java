package com.portfolio.schedule.domain.schedule.repository;
import com.portfolio.schedule.domain.schedule.entity.Schedule;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
    @Query("SELECT s FROM Schedule s JOIN FETCH s.user LEFT JOIN FETCH s.category WHERE s.user.id=:uid")
    Page<Schedule> findByUserId(@Param("uid") Long uid, Pageable p);
    @Query("SELECT s FROM Schedule s JOIN FETCH s.user LEFT JOIN FETCH s.category WHERE s.user.id=:uid AND s.category.id=:cid")
    Page<Schedule> findByUserIdAndCategoryId(@Param("uid") Long uid,@Param("cid") Long cid,Pageable p);
    Optional<Schedule> findByIdAndUserId(Long id,Long userId);
}
