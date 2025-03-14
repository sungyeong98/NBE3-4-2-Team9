package com.backend.domain.jobskill.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
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