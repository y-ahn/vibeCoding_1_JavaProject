package com.portfolio.schedule.domain.category.repository;
import com.portfolio.schedule.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CategoryRepository extends JpaRepository<Category,Long> {
    List<Category> findByUserId(Long userId);
    Optional<Category> findByIdAndUserId(Long id,Long userId);
}
