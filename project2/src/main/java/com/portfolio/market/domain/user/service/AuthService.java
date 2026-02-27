package com.portfolio.market.domain.user.service;

import com.portfolio.market.domain.user.dto.LoginRequest;
import com.portfolio.market.domain.user.dto.SignupRequest;
import com.portfolio.market.domain.user.dto.TokenResponse;
import com.portfolio.market.domain.user.entity.User;
import com.portfolio.market.domain.user.repository.UserRepository;
import com.portfolio.market.global.exception.CustomException;
import com.portfolio.market.global.exception.ErrorCode;
import com.portfolio.market.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public TokenResponse signup(SignupRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .nickname(req.getNickname())
                .build();
        userRepository.save(user);
        return new TokenResponse(
                jwtProvider.generateAccessToken(user.getId(), user.getEmail()),
                jwtProvider.generateRefreshToken(user.getId())
        );
    }

    public TokenResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        return new TokenResponse(
                jwtProvider.generateAccessToken(user.getId(), user.getEmail()),
                jwtProvider.generateRefreshToken(user.getId())
        );
    }
}
