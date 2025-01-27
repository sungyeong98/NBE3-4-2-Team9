package com.backend.domain.user.entity;

import com.backend.global.baseentity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SiteUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    @Email(message = "")
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "introduction", nullable = true)
    private String introduction;

    @Column(name = "job", nullable = true)
    private String job;

    @Column(name = "skill", nullable = true)
    private String skill;

    @Column(name = "user_role", nullable = false)
    private String userRole;

    private String kakaoId;

    private String profileImg;

    public void modifyProfile(String introduction, String job, String skill) {
        if (introduction != null) this.introduction = introduction;
        if (job != null) this.job = job;
        if (skill != null) this.skill = skill;
    }

    public SiteUser update(String name, String profileImg) {
        this.name = name;
        this.profileImg = profileImg;
        return this;
    }

}
