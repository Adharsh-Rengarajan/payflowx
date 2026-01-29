package com.payflowx.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
	private String token;
	private UserInfo user;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserInfo {
		private Long id;
		private String username;
		private String firstName;
		private String lastName;
		private String role;
	}
}
