package com.overcode250204.identityservice.service.impl;

import com.overcode250204.identityservice.dto.auth.AuthResponse;
import com.overcode250204.identityservice.dto.auth.LoginRequest;
import com.overcode250204.identityservice.dto.auth.MeResponse;
import com.overcode250204.identityservice.dto.auth.RegisterTenantRequest;
import com.overcode250204.identityservice.entity.AuthenticatedUser;
import com.overcode250204.identityservice.entity.User;
import com.overcode250204.identityservice.exception.UserInactiveException;
import com.overcode250204.identityservice.repository.UserRepository;
import com.overcode250204.identityservice.repository.UserRoleRepository;
import com.overcode250204.identityservice.service.IAuthService;
import com.overcode250204.identityservice.service.IJwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final static String HEADER_TOKEN_TYPE = "Bearer";

    private final IJwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse registerTenant(RegisterTenantRequest request) {
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!user.isActive()) {
            throw new UserInactiveException(email);
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        List<String> roles = userRoleRepository.findRoleCodesByUserId(user.getId());
        List<String> permissions = userRoleRepository.findPermissionCodesByUserId(user.getId());

        String accessToken = jwtTokenService.generateAccessToken(user.getId(), user.getTenantId(), user.getEmail(), roles, permissions);

        return new AuthResponse(accessToken, HEADER_TOKEN_TYPE,
                user.getId(), user.getTenantId(), user.getEmail(), user.getFullName(), roles, permissions);


    }

    @Override
    public MeResponse me(AuthenticatedUser user) {
        return new MeResponse(user.userId(), user.tenantId(), user.email(), user.roles(), user.permissions());
    }
}
