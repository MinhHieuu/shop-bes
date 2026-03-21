package com.beeshop.sd44.service;

import com.beeshop.sd44.dto.request.LoginRequest;
import com.beeshop.sd44.dto.response.LoginResponse;
import com.beeshop.sd44.entity.RefreshToken;
import com.beeshop.sd44.entity.User;
import com.beeshop.sd44.repository.RefreshTokenRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {
    private final JWTService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepo refreshTokenRepo;

    public AuthService(JWTService jwtService, UserService userService, PasswordEncoder passwordEncoder,
            RefreshTokenRepo refreshTokenRepo) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userService.getByEmail(loginRequest.getEmail());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return null;
        }
        String access = jwtService.createAccessToken(user.getRole(), user.getId().toString());
        String refresh = jwtService.createRefreshToken(user.getRole(), user.getId().toString());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refresh);
        refreshToken.setUser(user);
        refreshToken
                .setExpiryDate(new Date(System.currentTimeMillis() + jwtService.getRefreshExpirationSeconds() * 1000));
        refreshTokenRepo.save(refreshToken);

        return new LoginResponse(access, refresh, userService.buildRespone(user));
    }

    public String refreshAccessToken(String refreshToken) {
        return refreshTokenRepo.findByToken(refreshToken)
                .filter(rt -> !rt.isRevoked())
                .filter(rt -> rt.getExpiryDate() != null && rt.getExpiryDate().after(new Date()))
                .map(rt -> {
                    try {
                        String role = jwtService.getClaims(refreshToken).get("role", String.class);
                        String subject = jwtService.getClaims(refreshToken).getSubject();
                        return jwtService.createAccessToken(role, subject);
                    } catch (Exception ex) {
                        return null;
                    }
                }).orElse(null);
    }

    public boolean revokeRefreshToken(String refreshToken) {
        return refreshTokenRepo.findByToken(refreshToken).map(rt -> {
            rt.setRevoked(true);
            refreshTokenRepo.save(rt);
            return true;
        }).orElse(false);
    }
}
