package com.example.wallet_watch.ControllerTest;

import com.example.wallet_watch.Controller.UserController;
import com.example.wallet_watch.Model.User;
import com.example.wallet_watch.Service.UserService;
import com.example.wallet_watch.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Initialize mocks and build MockMvc manually
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void createUser_ShouldReturnCreatedUser_WhenValidRequest() throws Exception {
        User user = new User("John Doe", "john@example.com", "password", 1000.0);
        given(userService.createUser(user)).willReturn(user);

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"John Doe\", \"email\": \"john@example.com\", \"password\": \"password\", \"expenseLimit\": 1000.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void login_ShouldReturnToken_WhenLoginIsSuccessful() throws Exception {
        User user = new User("John Doe", "john@example.com", "password", 1000.0);
        given(userService.login(anyString(), anyString())).willReturn(Optional.of(user));
        given(jwtUtil.generateToken(anyString())).willReturn("jwt-token");

        mockMvc.perform(post("/users/login")
                        .param("email", "john@example.com")
                        .param("password", "password")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenLoginFails() throws Exception {
        given(userService.login(anyString(), anyString())).willReturn(Optional.empty());

        mockMvc.perform(post("/users/login")
                        .param("email", "wrong@example.com")
                        .param("password", "wrongpassword")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void fetchUserByEmail_ShouldReturnNotFound_WhenEmailDoesNotExist() throws Exception {
        given(userService.getUserByEmail("nonexistent@example.com")).willReturn(Optional.empty());

        mockMvc.perform(post("/users/get-user-by-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"nonexistent@example.com\""))
                .andExpect(status().isNotFound());
    }
}
