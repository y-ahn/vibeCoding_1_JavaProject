package com.portfolio.schedule.domain.category.service;

import com.portfolio.schedule.domain.category.dto.CategoryRequest;
import com.portfolio.schedule.domain.category.dto.CategoryResponse;
import com.portfolio.schedule.domain.category.entity.Category;
import com.portfolio.schedule.domain.category.repository.CategoryRepository;
import com.portfolio.schedule.domain.user.entity.User;
import com.portfolio.schedule.domain.user.repository.UserRepository;
import com.portfolio.schedule.global.exception.CustomException;
import com.portfolio.schedule.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public CategoryResponse create(Long userId, CategoryRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Category category = Category.builder()
                .user(user).name(req.getName()).color(req.getColor()).build();
        return CategoryResponse.from(categoryRepository.save(category));
    }

    public List<CategoryResponse> getMyCategories(Long userId) {
        return categoryRepository.findByUserId(userId).stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse update(Long userId, Long id, CategoryRequest req) {
        Category cat = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        cat.update(req.getName(), req.getColor());
        return CategoryResponse.from(cat);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        Category cat = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        categoryRepository.delete(cat);
    }
}
