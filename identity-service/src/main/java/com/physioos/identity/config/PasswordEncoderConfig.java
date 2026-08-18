package com.physioos.identity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Defaults: saltLength=16, hashLength=32, parallelism=1, memory=4096, iterations=3
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
