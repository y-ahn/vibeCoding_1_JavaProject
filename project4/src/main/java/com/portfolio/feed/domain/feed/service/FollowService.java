package com.portfolio.feed.domain.feed.service;

import com.portfolio.feed.domain.user.entity.Follow;
import com.portfolio.feed.domain.user.entity.User;
import com.portfolio.feed.domain.user.repository.FollowRepository;
import com.portfolio.feed.domain.user.repository.UserRepository;
import com.portfolio.feed.global.exception.CustomException;
import com.portfolio.feed.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId))
            throw new CustomException(ErrorCode.CANNOT_FOLLOW_SELF);
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId))
            throw new CustomException(ErrorCode.ALREADY_FOLLOWING);

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        followRepository.save(Follow.builder().follower(follower).following(following).build());
    }

    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        Follow follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        followRepository.delete(follow);
    }
}
