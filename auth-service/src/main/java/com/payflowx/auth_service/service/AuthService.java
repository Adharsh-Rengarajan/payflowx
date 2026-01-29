package com.payflowx.auth_service.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.payflowx.auth_service.dto.ApiResponse;
import com.payflowx.auth_service.dto.LoginRequestDTO;
import com.payflowx.auth_service.dto.LoginResponseDTO;
import com.payflowx.auth_service.entity.User;
import com.payflowx.auth_service.repository.UserRepository;
import com.payflowx.auth_service.util.JwtUtil;

@Service
public class AuthService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	public ResponseEntity<ApiResponse> login(LoginRequestDTO loginRequestDTO) {

		Optional<User> userOpt = userRepository.findByUsername(loginRequestDTO.getUsername());

		if (userOpt.isEmpty()) {
			Map<String, String> errors = new HashMap<>();
			errors.put("credentials", "Invalid email or password");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials", errors));
		}

		User user = userOpt.get();

		// Verify password
		if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
			Map<String, String> errors = new HashMap<>();
			errors.put("credentials", "Invalid email or password");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials", errors));
		}

		// Generate JWT token
		String token = jwtUtil.generateToken(user);

		// Create response
		LoginResponseDTO.UserInfo userInfo = new LoginResponseDTO.UserInfo(user.getId(), user.getUsername(),
				user.getFirstName(), user.getLastName(), user.getRole());

		LoginResponseDTO responseData = new LoginResponseDTO(token, userInfo);

		return ResponseEntity.ok().body(ApiResponse.success(HttpStatus.OK.value(), "Login successful", responseData));
	}

}
