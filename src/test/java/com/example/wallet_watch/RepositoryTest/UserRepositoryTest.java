package com.example.wallet_watch.RepositoryTest;

import com.example.wallet_watch.Model.User;
import com.example.wallet_watch.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // Test data
    private User createTestUser() {
        return new User("Vinay", "vinay@example.com", "password", 1000.0);
    }

    @Test
    public void testSaveUser() {
        // Arrange
        User user = createTestUser();

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertNotNull(savedUser.getUserId());
        assertEquals("Vinay", savedUser.getName());
        assertEquals("vinay@example.com", savedUser.getEmail());
        assertEquals("password", savedUser.getPassword());
        assertEquals(1000.0, savedUser.getExpenseLimit());
    }

    @Test
    public void testFindUserByEmail() {
        // Arrange
        User user = createTestUser();
        userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findByEmail("vinay@example.com");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("Vinay", foundUser.get().getName());
        assertEquals("vinay@example.com", foundUser.get().getEmail());
        assertEquals(1000.0, foundUser.get().getExpenseLimit());
    }

    @Test
    public void testExistsByEmail() {
        // Arrange
        User user = createTestUser();
        userRepository.save(user);

        // Act
        boolean exists = userRepository.existsByEmail("vinay@example.com");

        // Assert
        assertTrue(exists);
    }
}