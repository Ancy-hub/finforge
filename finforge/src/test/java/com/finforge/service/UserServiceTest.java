package com.finforge.service;

import com.finforge.dao.CategoryDAO;
import com.finforge.dao.UserDAO;
import com.finforge.dto.UserDTO;
import com.finforge.exception.DAOException;
import com.finforge.exception.DuplicateUserException;
import com.finforge.exception.InvalidCredentialsException;
import com.finforge.exception.UserNotFoundException;
import com.finforge.exception.ValidationException;
import com.finforge.model.Category;
import com.finforge.model.User;
import com.finforge.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock private UserDAO     userDAO;
    @Mock private CategoryDAO categoryDAO;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDAO, categoryDAO);
    }

    // -----------------------------------------------------------------------
    // register()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("register() - should save user and return with ID when all fields are valid")
    void register_shouldSaveUserAndReturn_whenValid() throws Exception {
        // Arrange
        UserDTO dto = validRegistrationDTO();
        User savedUser = buildUser(1);

        when(userDAO.existsByUsername("johndoe")).thenReturn(false);
        when(userDAO.existsByEmail("john@example.com")).thenReturn(false);
        when(userDAO.save(any(User.class))).thenReturn(savedUser);
        when(categoryDAO.save(any(Category.class))).thenReturn(new Category());

        // Act
        User result = userService.register(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        verify(userDAO).save(any(User.class));
        // Should seed 7 default categories
        verify(categoryDAO, times(7)).save(any(Category.class));
    }

    @Test
    @DisplayName("register() - should throw DuplicateUserException when username is taken")
    void register_shouldThrow_whenUsernameAlreadyTaken() throws Exception {
        // Arrange
        when(userDAO.existsByUsername("johndoe")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateUserException.class, () -> userService.register(validRegistrationDTO()));
        verify(userDAO, never()).save(any());
    }

    @Test
    @DisplayName("register() - should throw DuplicateUserException when email is taken")
    void register_shouldThrow_whenEmailAlreadyTaken() throws Exception {
        when(userDAO.existsByUsername("johndoe")).thenReturn(false);
        when(userDAO.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(DuplicateUserException.class, () -> userService.register(validRegistrationDTO()));
    }

    @Test
    @DisplayName("register() - should throw ValidationException when username is blank")
    void register_shouldThrow_whenUsernameBlank() {
        UserDTO dto = validRegistrationDTO();
        dto.setUsername("");

        assertThrows(ValidationException.class, () -> userService.register(dto));
    }

    @Test
    @DisplayName("register() - should throw ValidationException when passwords do not match")
    void register_shouldThrow_whenPasswordsMismatch() {
        UserDTO dto = validRegistrationDTO();
        dto.setConfirmPassword("WrongPassword1!");

        assertThrows(ValidationException.class, () -> userService.register(dto));
    }

    @Test
    @DisplayName("register() - should throw ValidationException when email is invalid")
    void register_shouldThrow_whenEmailInvalid() {
        UserDTO dto = validRegistrationDTO();
        dto.setEmail("not-an-email");

        assertThrows(ValidationException.class, () -> userService.register(dto));
    }

    // -----------------------------------------------------------------------
    // login()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("login() - should return user when credentials are valid")
    void login_shouldReturnUser_whenCredentialsValid() throws Exception {
        // Arrange
        String  rawPassword = "SecurePass1!";
        String  hash        = PasswordUtil.hashPassword(rawPassword);
        User    user        = buildUser(1);
        user.setPasswordHash(hash);

        when(userDAO.findByUsername("johndoe")).thenReturn(Optional.of(user));

        // Act
        User result = userService.login("johndoe", rawPassword);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getUserId());
    }

    @Test
    @DisplayName("login() - should throw InvalidCredentialsException when user not found")
    void login_shouldThrow_whenUserNotFound() throws Exception {
        when(userDAO.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login("unknown", "pass"));
    }

    @Test
    @DisplayName("login() - should throw InvalidCredentialsException when password is wrong")
    void login_shouldThrow_whenPasswordWrong() throws Exception {
        User user = buildUser(1);
        user.setPasswordHash(PasswordUtil.hashPassword("CorrectPass1!"));

        when(userDAO.findByUsername("johndoe")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login("johndoe", "WrongPassword!"));
    }

    @Test
    @DisplayName("login() - should throw InvalidCredentialsException when account inactive")
    void login_shouldThrow_whenAccountInactive() throws Exception {
        User user = buildUser(1);
        user.setActive(false);
        user.setPasswordHash(PasswordUtil.hashPassword("SecurePass1!"));

        when(userDAO.findByUsername("johndoe")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login("johndoe", "SecurePass1!"));
    }

    // -----------------------------------------------------------------------
    // changePassword()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("changePassword() - should update hash when current password is correct")
    void changePassword_shouldUpdateHash_whenCurrentPasswordCorrect() throws Exception {
        User user = buildUser(1);
        user.setPasswordHash(PasswordUtil.hashPassword("OldPass1!"));

        when(userDAO.findById(1)).thenReturn(Optional.of(user));

        userService.changePassword(1, "OldPass1!", "NewPass1!", "NewPass1!");

        verify(userDAO).updatePassword(eq(1), anyString());
    }

    @Test
    @DisplayName("changePassword() - should throw InvalidCredentialsException when current password wrong")
    void changePassword_shouldThrow_whenCurrentPasswordWrong() throws Exception {
        User user = buildUser(1);
        user.setPasswordHash(PasswordUtil.hashPassword("OldPass1!"));

        when(userDAO.findById(1)).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class,
                () -> userService.changePassword(1, "WrongOld!", "NewPass1!", "NewPass1!"));
    }

    // -----------------------------------------------------------------------
    // getProfile()
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getProfile() - should throw UserNotFoundException when user does not exist")
    void getProfile_shouldThrow_whenUserNotFound() throws Exception {
        when(userDAO.findById(999)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getProfile(999));
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private UserDTO validRegistrationDTO() {
        UserDTO dto = new UserDTO();
        dto.setUsername("johndoe");
        dto.setEmail("john@example.com");
        dto.setPassword("SecurePass1!");
        dto.setConfirmPassword("SecurePass1!");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPhone("1234567890");
        return dto;
    }

    private User buildUser(int id) {
        User u = new User();
        u.setUserId(id);
        u.setUsername("johndoe");
        u.setEmail("john@example.com");
        u.setFirstName("John");
        u.setLastName("Doe");
        u.setActive(true);
        return u;
    }
}
