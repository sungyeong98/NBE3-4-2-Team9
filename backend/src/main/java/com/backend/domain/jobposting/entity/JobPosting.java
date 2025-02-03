package com.backend.domain.jobposting.entity;

import com.backend.domain.jobskill.entity.JobSkill;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JobPosting
 * <p>채용 공고 엔티티 입니다.</p>
 *
 * @author Kim Dong O
 */
@Entity
@Table(name = "job_posting")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class JobPosting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "job_posting_id")
	private Long id;
	@Column(nullable = false)
	private String subject; //제목
	@Column(nullable = false)
	private String url; //url

	@Column(nullable = false)
	private ZonedDateTime postDate; //작성 날짜
	@Column(nullable = false)
	private ZonedDateTime openDate; //공개 날짜
	@Column(nullable = false)
	private ZonedDateTime closeDate; //마감 날짜

	@Column(nullable = false)
	private String companyName; //회사 이름
	private String companyLink; //회사 링크

	@Embedded
	private ExperienceLevel experienceLevel; //직무 경력

	private String requireEducate; //학력 정보 (추후 Enum으로 수정 예정)

	@Enumerated(EnumType.STRING)
	private JobPostingStatus jobPostingStatus; //공고 상태

	@Embedded
	private Salary salary; //연봉 추후 Enum으로 관리

	@OneToMany
	private List<JobSkill> jobSkillList;
}
