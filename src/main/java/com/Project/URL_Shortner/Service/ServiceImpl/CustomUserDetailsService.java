package com.Project.URL_Shortner.Service.ServiceImpl;

import com.Project.URL_Shortner.Entities.UserEntity;
import com.Project.URL_Shortner.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user details for email: {}", email);
        try {
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found with email: {}", email);
                        return new UsernameNotFoundException("User not found with email: " + email);
                    });
            log.debug("User found and loaded successfully: {}", email);
            return user;
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error loading user by email: {} - {}", email, e.getMessage());
            throw new UsernameNotFoundException("Error loading user: " + email, e);
        }
    }
}