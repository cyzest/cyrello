package com.cyzest.cyrello.service;

import com.cyzest.cyrello.dao.User;
import com.cyzest.cyrello.dao.UserRepository;
import com.cyzest.cyrello.dto.DefaultAuthUser;
import com.cyzest.cyrello.dto.UserInfo;
import com.cyzest.cyrello.exception.BasedException;
import com.cyzest.cyrello.exception.UserExceptionType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserInfo registerUser(String id, String password) throws BasedException {

        if (userRepository.existsById(id)) {
            throw new BasedException(UserExceptionType.EXIST_USER);
        }

        User user = new User();

        user.setId(id);
        user.setPassword(passwordEncoder.encode(password));
        user.setRegDate(LocalDateTime.now());

        return new UserInfo(userRepository.saveAndFlush(user));
    }

    @Override
    public UserDetails loadUserByUsername(String id) {
        return userRepository.findById(id)
                .map(DefaultAuthUser::new)
                .orElseThrow(() -> new UsernameNotFoundException(id + " not found"));
    }

}
