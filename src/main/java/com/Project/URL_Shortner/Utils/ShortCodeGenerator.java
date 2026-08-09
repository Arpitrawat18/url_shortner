package com.Project.URL_Shortner.Utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Slf4j
public class ShortCodeGenerator {

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final int LENGTH = 6;

    private final Random random = new Random();

    public String generateShortCode() {
        log.debug("Generating new short code");
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        log.debug("Short code generated: {}", code);
        return code.toString();
    }
}
