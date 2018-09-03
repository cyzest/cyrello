package com.cyzest.cyrello.service;

import com.cyzest.cyrello.dao.User;
import com.cyzest.cyrello.dao.UserRepository;
import com.cyzest.cyrello.dto.DefaultAuthUser;
import com.cyzest.cyrello.dto.UserInfo;
import com.cyzest.cyrello.dto.UserRegParam;
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
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserInfo registerUser(UserRegParam userRegParam) throws Exception {

        String email = userRegParam.getEmail();
        String password = userRegParam.getPassword();

        if (userRepository.existsByEmail(email)) {
            throw new BasedException(UserExceptionType.EXIST_USER);
        }

        User user = new User();

        user.setId(UUID.randomUUID().toString().replace("-", ""));
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRegisterDate(LocalDateTime.now());

        return new UserInfo(userRepository.saveAndFlush(user));
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .map(DefaultAuthUser::new)
                .orElseThrow(() -> new UsernameNotFoundException(email + " not found"));
    }

}
