package com.Project.URL_Shortner.Repository;

import com.Project.URL_Shortner.Entities.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
    Optional<UrlEntity> findByShortCode(String shortCode);
    List<UrlEntity> findByUser_Id(Long userId);
    Optional<UrlEntity> findByShortCodeAndUser_Id(String shortCode, Long userId);
    boolean existsByShortCode(String shortCode);
    @Modifying
    @Transactional
    @Query("""
        DELETE FROM UrlEntity u
        WHERE u.expiresAt < CURRENT_TIMESTAMP
        """)
    int deleteExpiredUrls();


}
