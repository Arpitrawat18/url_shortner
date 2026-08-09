package com.Project.URL_Shortner.Repository;

import com.Project.URL_Shortner.Entities.AnalyticsEntity;
import com.Project.URL_Shortner.Entities.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface AnalyticsRepository extends JpaRepository<AnalyticsEntity, Long> {

    // use boxed Long to be consistent with other count methods and allow null if needed
    Long countByUrl(UrlEntity url);
    @Query("""
        SELECT COUNT(DISTINCT a.ipAddress)
        FROM AnalyticsEntity a
        WHERE a.url = :url
    """)
    Long countDistinctIpAddressByUrl(@Param("url") UrlEntity url);

    @Query(value = """
        SELECT browser
        FROM analytics
        WHERE url_id = :urlId
          AND browser IS NOT NULL
        GROUP BY browser
        ORDER BY COUNT(*) DESC
        LIMIT 1
        """, nativeQuery = true)
    String findTopBrowser(@Param("urlId") Long urlId);

    @Query(value = """
        SELECT device
        FROM analytics
        WHERE url_id = :urlId
          AND device IS NOT NULL
        GROUP BY device
        ORDER BY COUNT(*) DESC
        LIMIT 1
        """, nativeQuery = true)
    String findTopDevice(@Param("urlId") Long urlId);

    @Query(value = """
        SELECT operating_system
        FROM analytics
        WHERE url_id = :urlId
          AND operating_system IS NOT NULL
        GROUP BY operating_system
        ORDER BY COUNT(*) DESC
        LIMIT 1
        """, nativeQuery = true)
    String findTopOperatingSystem(@Param("urlId") Long urlId);

    @Query(value = """
        SELECT referrer
        FROM analytics
        WHERE url_id = :urlId
          AND referrer IS NOT NULL
        GROUP BY referrer
        ORDER BY COUNT(*) DESC
        LIMIT 1
        """, nativeQuery = true)
    String findTopReferrer(@Param("urlId") Long urlId);

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM AnalyticsEntity a
        WHERE a.url.expiresAt < CURRENT_TIMESTAMP
        """)
    int deleteAnalyticsForExpiredUrls();

}


