package com.payflowx.auth_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Getter
@Setter
@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq_gen")
	@SequenceGenerator(name = "users_seq_gen", sequenceName = "users_seq", allocationSize = 1)
	private Long id;

	@Column(nullable = false, unique = false, length = 320)
	private String firstName;

	@Column(nullable = false, unique = false, length = 320)
	private String lastName;

	@Column(nullable = false, unique = true, length = 320)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String role;

}
