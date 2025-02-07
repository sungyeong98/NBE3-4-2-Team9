package com.backend.domain.jobskill.repository;

import com.backend.domain.jobskill.entity.JobSkill;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobSkillJpaRepository extends JpaRepository<JobSkill, Long> {
	@Query("select js from JobSkill js where js.code = :code")
	Optional<JobSkill> findByCode(@Param("code") Integer code);
	Optional<JobSkill> findByName(String name);
}
