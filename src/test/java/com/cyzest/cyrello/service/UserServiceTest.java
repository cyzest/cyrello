package com.cyzest.cyrello.service;

import com.cyzest.cyrello.dao.User;
import com.cyzest.cyrello.dao.UserRepository;
import com.cyzest.cyrello.dto.UserInfo;
import com.cyzest.cyrello.dto.UserRegParam;
import com.cyzest.cyrello.exception.BasedException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User defaultUser;

    @BeforeEach
    public void setup() {
        defaultUser = new User("id","cyzest@nate.com", "password", LocalDateTime.now());
    }

    @Test
    public void loadUserByUsernameTest() {

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(defaultUser));

        UserDetails userDetails = userService.loadUserByUsername("cyzest@nate.com");

        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals("id", userDetails.getUsername());

        verify(userRepository, times(1)).findByEmail(anyString());

        clearInvocations(userRepository);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("cyzest@nate.com"));

        verify(userRepository, times(1)).findByEmail(anyString());

        clearInvocations(userRepository);

        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(null));

        verify(userRepository, times(1)).findByEmail(null);
    }

    @Test
    public void registerUserTest() throws Exception {

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(defaultUser);

        UserInfo userInfo = userService.registerUser(new UserRegParam(anyString(), "password"));

        Assertions.assertNotNull(userInfo);
        Assertions.assertEquals("id", userInfo.getId());

        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));

        clearInvocations(userRepository);

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        Assertions.assertThrows(
                BasedException.class,
                () -> userService.registerUser(new UserRegParam(anyString(), "password")));

        verify(userRepository, times(1)).existsByEmail(anyString());

        clearInvocations(userRepository);

        when(userRepository.existsByEmail(null)).thenReturn(false);
        when(userRepository.saveAndFlush(any(User.class))).thenThrow(new NullPointerException());

        Assertions.assertThrows(
                NullPointerException.class,
                () -> userService.registerUser(new UserRegParam(null, anyString())));

        verify(userRepository, times(1)).existsByEmail(null);
        verify(userRepository, times(1)).saveAndFlush(any(User.class));

        clearInvocations(userRepository);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        Assertions.assertThrows(
                NullPointerException.class,
                () -> userService.registerUser(new UserRegParam(anyString(), null)));

        verify(userRepository, times(1)).existsByEmail(anyString());
    }

}
