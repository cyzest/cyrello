package com.cyzest.cyrello.service;

import com.cyzest.cyrello.dao.User;
import com.cyzest.cyrello.dao.UserRepository;
import com.cyzest.cyrello.dto.UserInfo;
import com.cyzest.cyrello.exception.BasedException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private User defaultUser;

    @BeforeEach
    public void setup() {
        userService = new UserService(userRepository, new BCryptPasswordEncoder());
        defaultUser = new User("cyzest@nate.com", "password", LocalDateTime.now());
    }

    @Test
    public void loadUserByUsernameTest() {

        when(userRepository.findById(anyString())).thenReturn(Optional.of(defaultUser));

        UserDetails userDetails = userService.loadUserByUsername("cyzest@nate.com");

        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals("cyzest@nate.com", userDetails.getUsername());

        verify(userRepository, times(1)).findById(anyString());

        clearInvocations(userRepository);

        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("cyzest@nate.com"));

        verify(userRepository, times(1)).findById(anyString());

        clearInvocations(userRepository);

        when(userRepository.findById(null)).thenThrow(InvalidDataAccessApiUsageException.class);

        Assertions.assertThrows(
                InvalidDataAccessApiUsageException.class,
                () -> userService.loadUserByUsername(null));

        verify(userRepository, times(1)).findById(null);
    }

    @Test
    public void registerUserTest() throws Exception {

        when(userRepository.existsById(anyString())).thenReturn(false);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(defaultUser);

        UserInfo userInfo = userService.registerUser("cyzest@nate.com", "password");

        Assertions.assertNotNull(userInfo);
        Assertions.assertEquals("cyzest@nate.com", userInfo.getId());

        verify(userRepository, times(1)).existsById(anyString());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));

        clearInvocations(userRepository);

        when(userRepository.existsById(anyString())).thenReturn(true);

        Assertions.assertThrows(
                BasedException.class,
                () -> userService.registerUser("cyzest@nate.com", "password"));

        verify(userRepository, times(1)).existsById(anyString());

        clearInvocations(userRepository);

        when(userRepository.existsById(null)).thenThrow(InvalidDataAccessApiUsageException.class);

        Assertions.assertThrows(
                InvalidDataAccessApiUsageException.class,
                () -> userService.registerUser(null, "password"));

        verify(userRepository, times(1)).existsById(null);

        clearInvocations(userRepository);

        when(userRepository.existsById(anyString())).thenReturn(false);

        Assertions.assertThrows(
                NullPointerException.class,
                () -> userService.registerUser("cyzest@nate.com", null));

        verify(userRepository, times(1)).existsById(anyString());
    }

}
