package com.Project.URL_Shortner.Config;

import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserAgentConfig {

    @Bean
    public UserAgentAnalyzer userAgentAnalyzer() {
        return UserAgentAnalyzer
                .newBuilder()
                .hideMatcherLoadStats()
                .build();
    }
}