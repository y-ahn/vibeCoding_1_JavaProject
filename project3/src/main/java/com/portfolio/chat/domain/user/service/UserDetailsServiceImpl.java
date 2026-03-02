package com.portfolio.chat.domain.user.service;

import com.portfolio.chat.domain.user.repository.UserRepository;
import com.portfolio.chat.global.exception.CustomException;
import com.portfolio.chat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        com.portfolio.chat.domain.user.entity.User user =
                userRepository.findById(Long.parseLong(userId))
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return new User(String.valueOf(user.getId()), user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
