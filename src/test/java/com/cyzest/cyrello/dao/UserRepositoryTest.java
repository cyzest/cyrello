package com.cyzest.cyrello.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@DataJpaTest
@ExtendWith(SpringExtension.class)
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void existsByIdTest() {
        entityManager.persistAndFlush(new User("id", "cyzest@nate.com", "password", LocalDateTime.now()));
        Assertions.assertTrue(userRepository.existsById("id"));
        Assertions.assertFalse(userRepository.existsById("id2"));
        Assertions.assertThrows(InvalidDataAccessApiUsageException.class, () -> userRepository.existsById(null));
    }

    @Test
    public void findByIdTest() {
        entityManager.persistAndFlush(new User("id", "cyzest@nate.com", "password", LocalDateTime.now()));
        Optional<User> userOptional = userRepository.findById("id");
        Assertions.assertTrue(userOptional.isPresent());
        Assertions.assertEquals("id", userOptional.get().getId());
        Assertions.assertFalse(userRepository.findById("id2").isPresent());
        Assertions.assertThrows(InvalidDataAccessApiUsageException.class, () -> userRepository.findById(null));
    }

    @Test
    public void existsByEmailTest() {
        entityManager.persistAndFlush(new User("id", "cyzest@nate.com", "password", LocalDateTime.now()));
        Assertions.assertTrue(userRepository.existsByEmail("cyzest@nate.com"));
        Assertions.assertFalse(userRepository.existsByEmail("cyzest2@nate.com"));
        Assertions.assertFalse(userRepository.existsByEmail(null));
    }

    @Test
    public void findByEmailTest() {
        entityManager.persistAndFlush(new User("id", "cyzest@nate.com", "password", LocalDateTime.now()));
        Optional<User> userOptional = userRepository.findByEmail("cyzest@nate.com");
        Assertions.assertTrue(userOptional.isPresent());
        Assertions.assertEquals("cyzest@nate.com", userOptional.get().getEmail());
        Assertions.assertFalse(userRepository.findByEmail("cyzest2@nate.com").isPresent());
        Assertions.assertFalse(userRepository.findByEmail(null).isPresent());
    }

    @Test
    public void saveTest() {
        User user = new User("id", "cyzest@nate.com", "password", LocalDateTime.now());
        User userEntity = userRepository.saveAndFlush(user);
        Assertions.assertNotNull(userEntity);
        Assertions.assertEquals(user.getId(), userEntity.getId());
        Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(
                        new User("id2", "cyzest@nate.com", "password", LocalDateTime.now())));
        Assertions.assertThrows(
                JpaSystemException.class,
                () -> userRepository.saveAndFlush(
                        new User(null, "cyzest3@nate.com", "password", LocalDateTime.now())));
        Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(
                        new User("id4", null, "password", LocalDateTime.now())));
        Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(
                        new User("id5", "cyzest5@nate.com", null, LocalDateTime.now())));
        Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(
                        new User("id6", "cyzest6@nate.com", "password", null)));
    }

}
