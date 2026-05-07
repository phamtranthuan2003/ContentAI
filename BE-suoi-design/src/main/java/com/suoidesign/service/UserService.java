package com.suoidesign.service;

import com.suoidesign.dto.AuthResponse;
import com.suoidesign.dto.LoginRequest;
import com.suoidesign.dto.RegisterRequest;
import com.suoidesign.entity.Role;
import com.suoidesign.entity.User;
import com.suoidesign.repository.UserRepository;
import com.suoidesign.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(Role.USER);
        user.setIsActive(true);
        
        user = userRepository.save(user);
        
        String token = jwtUtil.generateToken(user);
        
        return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }
        
        String token = jwtUtil.generateToken(user);
        
        return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
