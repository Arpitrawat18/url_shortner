package com.Project.URL_Shortner.Service.ServiceImpl;

import com.Project.URL_Shortner.Dto.request.CreateUrlRequest;
import com.Project.URL_Shortner.Dto.response.AnalyticsResponse;
import com.Project.URL_Shortner.Dto.response.UrlResponse;
import com.Project.URL_Shortner.Entities.UrlEntity;
import com.Project.URL_Shortner.Entities.UserEntity;
import com.Project.URL_Shortner.Exception.UrlNotFoundException;
import com.Project.URL_Shortner.Kafka.producer.AnalyticsProducer;
import com.Project.URL_Shortner.Repository.AnalyticsRepository;
import com.Project.URL_Shortner.Repository.UrlRepository;
import com.Project.URL_Shortner.Utils.ShortCodeGenerator;
import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplOwnershipTest {

    private static final UserEntity USER_A = UserEntity.builder()
            .id(1L)
            .name("User A")
            .email("a@example.com")
            .password("encoded-a")
            .build();

    private static final UserEntity USER_B = UserEntity.builder()
            .id(2L)
            .name("User B")
            .email("b@example.com")
            .password("encoded-b")
            .build();

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    @Mock
    private RedisTemplate<String, UrlEntity> redisTemplate;

    @Mock
    private ValueOperations<String, UrlEntity> valueOperations;

    private static final UserAgentAnalyzer USER_AGENT_ANALYZER =
            UserAgentAnalyzer.newBuilder()
                    .hideMatcherLoadStats()
                    .build();

    @Mock
    private AnalyticsRepository analyticsRepository;

    @Mock
    private AnalyticsProducer analyticsProducer;

    @Mock
    private HttpServletRequest request;

    private UrlServiceImpl urlService;

    @BeforeEach
    void setUp() {
        urlService = new UrlServiceImpl(
                urlRepository,
                shortCodeGenerator,
                redisTemplate,
                analyticsRepository,
                USER_AGENT_ANALYZER,
                analyticsProducer
        );
        ReflectionTestUtils.setField(urlService, "baseUrl", "http://localhost:8080");
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private static void authenticate(UserEntity user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
    }

    private static void authenticateAnonymously() {
        SecurityContextHolder.getContext().setAuthentication(
                new AnonymousAuthenticationToken(
                        "anonymous",
                        "anonymousUser",
                        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
                )
        );
    }

    private static UrlEntity urlEntity(Long id, String shortCode, UserEntity owner) {
        return UrlEntity.builder()
                .id(id)
                .originalUrl("https://example.com/" + shortCode)
                .shortCode(shortCode)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .user(owner)
                .build();
    }

    private static CreateUrlRequest createRequest(String url) {
        return CreateUrlRequest.builder()
                .originalUrl(url)
                .build();
    }

    @Test
    void userA_createsUrl_ownerComesFromSecurityContext() {
        when(shortCodeGenerator.generateShortCode()).thenReturn("aaaaaa");
        when(urlRepository.existsByShortCode("aaaaaa")).thenReturn(false);
        when(urlRepository.save(any(UrlEntity.class))).thenReturn(urlEntity(1L, "aaaaaa", USER_A));

        authenticate(USER_A);
        urlService.createShortUrl(createRequest("https://example.com/aaaaaa"));

        ArgumentCaptor<UrlEntity> captor = ArgumentCaptor.forClass(UrlEntity.class);
        verify(urlRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isNotNull();
        assertThat(captor.getValue().getUser().getId()).isEqualTo(USER_A.getId());
    }

    @Test
    void userA_canSeeOwnUrlInMyUrls() {
        authenticate(USER_A);
        when(urlRepository.findByUser_Id(USER_A.getId()))
                .thenReturn(List.of(urlEntity(1L, "aaaaaa", USER_A)));

        List<UrlResponse> urls = urlService.getMyUrls();

        assertThat(urls).extracting(UrlResponse::getShortCode).containsExactly("aaaaaa");
        verify(urlRepository).findByUser_Id(USER_A.getId());
    }

    @Test
    void userB_cannotSeeUserAsUrlInMyUrls() {
        authenticate(USER_B);
        when(urlRepository.findByUser_Id(USER_B.getId()))
                .thenReturn(List.of(urlEntity(2L, "bbbbbb", USER_B)));

        List<UrlResponse> urls = urlService.getMyUrls();

        assertThat(urls).extracting(UrlResponse::getShortCode).doesNotContain("aaaaaa");
        verify(urlRepository).findByUser_Id(USER_B.getId());
        verify(urlRepository, never()).findByUser_Id(USER_A.getId());
    }

    @Test
    void userA_canDeleteOwnUrl() {
        authenticate(USER_A);
        UrlEntity urlA = urlEntity(1L, "aaaaaa", USER_A);
        when(urlRepository.findByShortCodeAndUser_Id("aaaaaa", USER_A.getId()))
                .thenReturn(Optional.of(urlA));

        urlService.deleteUrl("aaaaaa");

        verify(urlRepository).delete(urlA);
        verify(redisTemplate).delete("aaaaaa");
    }

    @Test
    void userB_cannotDeleteUserAsUrl_returnsNotFound() {
        authenticate(USER_B);
        when(urlRepository.findByShortCodeAndUser_Id("aaaaaa", USER_B.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlService.deleteUrl("aaaaaa"))
                .isInstanceOf(UrlNotFoundException.class)
                .hasMessage("Short URL not found");

        verify(urlRepository, never()).delete(any(UrlEntity.class));
    }

    @Test
    void userA_canAccessOwnAnalytics() {
        authenticate(USER_A);
        UrlEntity urlA = urlEntity(1L, "aaaaaa", USER_A);
        when(urlRepository.findByShortCodeAndUser_Id("aaaaaa", USER_A.getId()))
                .thenReturn(Optional.of(urlA));
        when(analyticsRepository.countByUrl(urlA)).thenReturn(5L);
        when(analyticsRepository.countDistinctIpAddressByUrl(urlA)).thenReturn(3L);
        when(analyticsRepository.findTopBrowser(1L)).thenReturn("Chrome");
        when(analyticsRepository.findTopDevice(1L)).thenReturn("Desktop");
        when(analyticsRepository.findTopOperatingSystem(1L)).thenReturn("Windows");
        when(analyticsRepository.findTopReferrer(1L)).thenReturn("https://google.com");

        AnalyticsResponse response = urlService.getAnalytics("aaaaaa");

        assertThat(response.getShortCode()).isEqualTo("aaaaaa");
        assertThat(response.getTotalClicks()).isEqualTo(5L);
        assertThat(response.getUniqueVisitors()).isEqualTo(3L);
        assertThat(response.getTopBrowser()).isEqualTo("Chrome");
    }

    @Test
    void userB_cannotAccessUserAsAnalytics_returnsNotFound() {
        authenticate(USER_B);
        when(urlRepository.findByShortCodeAndUser_Id("aaaaaa", USER_B.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlService.getAnalytics("aaaaaa"))
                .isInstanceOf(UrlNotFoundException.class)
                .hasMessage("Short URL not found");

        verify(analyticsRepository, never()).countByUrl(any(UrlEntity.class));
    }

    @Test
    void anonymousUser_canCreateUrl_withoutOwner() {
        when(shortCodeGenerator.generateShortCode()).thenReturn("cccccc");
        when(urlRepository.existsByShortCode("cccccc")).thenReturn(false);
        when(urlRepository.save(any(UrlEntity.class))).thenReturn(urlEntity(3L, "cccccc", null));

        authenticateAnonymously();
        UrlResponse response = urlService.createShortUrl(createRequest("https://example.com/cccccc"));

        ArgumentCaptor<UrlEntity> captor = ArgumentCaptor.forClass(UrlEntity.class);
        verify(urlRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isNull();
        assertThat(response.getShortCode()).isEqualTo("cccccc");
    }

    @Test
    void unauthenticatedUser_canCreateUrl_withoutOwner() {
        when(shortCodeGenerator.generateShortCode()).thenReturn("dddddd");
        when(urlRepository.existsByShortCode("dddddd")).thenReturn(false);
        when(urlRepository.save(any(UrlEntity.class))).thenReturn(urlEntity(4L, "dddddd", null));

        SecurityContextHolder.clearContext();
        urlService.createShortUrl(createRequest("https://example.com/dddddd"));

        ArgumentCaptor<UrlEntity> captor = ArgumentCaptor.forClass(UrlEntity.class);
        verify(urlRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isNull();
    }

    @Test
    void anonymousUser_canUsePublicRedirect() {
        UrlEntity urlA = urlEntity(1L, "aaaaaa", null);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("aaaaaa")).thenReturn(null);
        when(urlRepository.findByShortCode("aaaaaa")).thenReturn(Optional.of(urlA));
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        String originalUrl = urlService.getOriginalUrl("aaaaaa", request);

        assertThat(originalUrl).isEqualTo("https://example.com/aaaaaa");
        verify(analyticsProducer).sendAnalyticsEvent(any());
    }

    @Test
    void unauthenticatedMyUrls_isDenied() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> urlService.getMyUrls())
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void authenticatedUser_usesPrincipalIdentity_notAnyRequestValue() {
        authenticate(USER_A);
        UrlEntity urlA = urlEntity(1L, "aaaaaa", USER_A);
        when(urlRepository.findByShortCodeAndUser_Id("aaaaaa", USER_A.getId()))
                .thenReturn(Optional.of(urlA));

        urlService.deleteUrl("aaaaaa");

        verify(urlRepository).findByShortCodeAndUser_Id("aaaaaa", USER_A.getId());
        verify(urlRepository, never()).findByShortCodeAndUser_Id("aaaaaa", USER_B.getId());
    }
}
