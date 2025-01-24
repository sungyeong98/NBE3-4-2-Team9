package com.backend.domain.user.repository;

import com.backend.domain.user.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser, Long> {
    Optional<SiteUser> findByEmail(String email);
    Optional<SiteUser> findByApiKey(String apiKey);
}
