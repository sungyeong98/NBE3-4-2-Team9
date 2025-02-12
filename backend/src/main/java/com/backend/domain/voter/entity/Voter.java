package com.backend.domain.voter.entity;

import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.post.entity.Post;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.voter.domain.VoterType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "voter")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Voter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "voter_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_posting_id")
	private JobPosting jobPosting;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "site_user_id")
	private SiteUser siteUser;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private VoterType voterType;

	//TODO 추후 게시글 필드도 추가할 예정
}
