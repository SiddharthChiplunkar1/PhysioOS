package com.physioos.identity.service;

import com.physioos.identity.dto.AuthResponse;
import com.physioos.identity.dto.LoginRequest;
import com.physioos.identity.dto.RegisterRequest;
import com.physioos.identity.entity.RefreshToken;
import com.physioos.common.entity.Role;
import com.physioos.identity.entity.User;
import com.physioos.identity.repository.RefreshTokenRepository;
import com.physioos.identity.repository.UserRepository;
import com.physioos.identity.security.CustomUserDetails;
import com.physioos.identity.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User mockUser;
    private CustomUserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "refreshTokenDurationMs", 604800000L); // 7 days

        mockUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .password("hashed_password")
                .role(Role.DOCTOR)
                .organizationId(UUID.randomUUID())
                .status("ACTIVE")
                .build();

        mockUserDetails = new CustomUserDetails(mockUser);
    }

    @Test
    void testLogin_Success() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        Authentication auth = new UsernamePasswordAuthenticationToken(mockUserDetails, null, mockUserDetails.getAuthorities());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(tokenProvider.generateToken(mockUserDetails)).thenReturn("mock_jwt_token");
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("mock_jwt_token", response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());

        verify(refreshTokenRepository).deleteByUser_Id(mockUser.getId());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void testRegister_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("Password123!");
        request.setRole(Role.PATIENT);
        request.setOrganizationId(UUID.randomUUID());

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("new_hashed_password");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // Mock login behavior triggered after registration
        Authentication auth = new UsernamePasswordAuthenticationToken(mockUserDetails, null, mockUserDetails.getAuthorities());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(tokenProvider.generateToken(any())).thenReturn("mock_jwt_token");
        when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(mockUser));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRefreshToken_Success() {
        // Arrange
        String oldTokenStr = "valid-refresh-token";
        RefreshToken validToken = new RefreshToken(UUID.randomUUID(), mockUser, oldTokenStr, Instant.now().plusSeconds(3600), false);
        
        when(refreshTokenRepository.findByToken(oldTokenStr)).thenReturn(Optional.of(validToken));
        when(tokenProvider.generateToken(any(CustomUserDetails.class))).thenReturn("new_mock_jwt_token");

        // Act
        AuthResponse response = authService.refreshToken(oldTokenStr);

        // Assert
        assertNotNull(response);
        assertEquals("new_mock_jwt_token", response.getAccessToken());
        assertEquals(oldTokenStr, response.getRefreshToken());
    }

    @Test
    void testRefreshToken_Expired() {
        // Arrange
        String oldTokenStr = "expired-refresh-token";
        RefreshToken expiredToken = new RefreshToken(UUID.randomUUID(), mockUser, oldTokenStr, Instant.now().minusSeconds(3600), false);
        
        when(refreshTokenRepository.findByToken(oldTokenStr)).thenReturn(Optional.of(expiredToken));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> authService.refreshToken(oldTokenStr));
        assertTrue(exception.getMessage().contains("expired"));
        verify(refreshTokenRepository).delete(expiredToken);
    }
}
