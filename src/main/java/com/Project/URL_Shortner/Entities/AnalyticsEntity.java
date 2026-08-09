package com.Project.URL_Shortner.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "analytics")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "url_id", nullable = false)
    private UrlEntity url;

    @Column(nullable = false)
    private LocalDateTime clickedAt;
    

    private String ipAddress;

    private String userAgent;

    private String browser;

    private String device;

    private String operatingSystem;

    private String referrer;
}
