package com.Buddymate.pickMate.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class EnvConfig {

    @Bean
    public StandardEnvironment environment() {
        StandardEnvironment environment = new StandardEnvironment();

        // `.env` 파일 로드
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        Map<String, Object> envVars = new HashMap<>();

        dotenv.entries().forEach(entry -> envVars.put(entry.getKey(), entry.getValue()));

        // 환경 변수 등록
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.addFirst(new MapPropertySource("dotenv", envVars));

        return environment;
    }
}
