package com.backend.domain.post.entity;

import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.user.entity.SiteUser;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@DiscriminatorValue("recruitment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentPost extends Post {

    private ZonedDateTime recruitmentClosingDate;
    private Integer numOfApplicants;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private RecruitmentStatus recruitmentStatus;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = true)
    private JobPosting jobPosting;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private SiteUser author;

	// 게시글 수정(모집 게시판)
	public void updatePost(String subject, String content, Integer numOfApplicants) {
		super.updatePost(subject, content);
		this.numOfApplicants = numOfApplicants;
	}

}
