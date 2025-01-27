package com.example.wallet_watch.ServiceTest;

import com.example.wallet_watch.Model.User;
import com.example.wallet_watch.Repository.UserRepository;
import com.example.wallet_watch.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_ShouldSaveAndReturnUser() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo("test@example.com");

        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void login_ShouldReturnUser_WhenEmailAndPasswordMatch() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        Optional<User> result = userService.login("test@example.com", "password");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");

        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void login_ShouldReturnEmpty_WhenEmailNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        Optional<User> result = userService.login("test@example.com", "password");

        assertThat(result).isNotPresent();

        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void login_ShouldReturnEmpty_WhenPasswordDoesNotMatch() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        Optional<User> result = userService.login("test@example.com", "wrongpassword");

        assertThat(result).isNotPresent();

        verify(userRepository, times(1)).findByEmail(anyString());
    }
}
