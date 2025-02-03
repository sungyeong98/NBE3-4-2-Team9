package com.backend.domain.jobskill.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "job_skill")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class JobSkill {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "job_skill_id")
	private Long id;

	@Column(name = "job_skill_name", length = 30, unique = true, nullable = false)
	private String name;

	@Column(name = "job_skill_code", unique = true, nullable = false)
	private Integer code;
}