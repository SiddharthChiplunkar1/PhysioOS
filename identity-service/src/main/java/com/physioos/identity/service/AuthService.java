package com.physioos.identity.service;

import com.physioos.identity.dto.AuthResponse;
import com.physioos.identity.dto.LoginRequest;
import com.physioos.identity.dto.RegisterRequest;
import com.physioos.identity.entity.RefreshToken;
import com.physioos.identity.entity.User;
import com.physioos.identity.repository.RefreshTokenRepository;
import com.physioos.identity.repository.UserRepository;
import com.physioos.identity.security.CustomUserDetails;
import com.physioos.identity.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh.expiration:604800000}") // 7 days
    private long refreshTokenDurationMs;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = tokenProvider.generateToken(userDetails);
        
        // Revoke any existing refresh tokens for this user
        refreshTokenRepository.deleteByUser_Id(userDetails.getId());
        
        // Create new refresh token
        RefreshToken refreshToken = createRefreshToken(userDetails.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .organizationId(request.getOrganizationId())
                .status("ACTIVE")
                .build();

        userRepository.save(user);

        // Auto login after registration
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(request.getEmail());
        loginRequest.setPassword(request.getPassword());
        return login(loginRequest);
    }

    @Transactional
    public AuthResponse refreshToken(String requestRefreshToken) {
        return refreshTokenRepository.findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    CustomUserDetails userDetails = new CustomUserDetails(user);
                    String accessToken = tokenProvider.generateToken(userDetails);
                    return AuthResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(requestRefreshToken)
                            .tokenType("Bearer")
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        if (token.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked.");
        }
        return token;
    }

    private RefreshToken createRefreshToken(UUID userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findById(userId).orElseThrow());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }
}
