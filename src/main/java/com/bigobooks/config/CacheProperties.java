package com.bigobooks.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "cache")
@Getter
@Setter

// Esta es una clase auxiliar para extraer los ttl de mi aplication.properties y
// utilizarlos en mi RedisConfig.java
public class CacheProperties {

    // El nombre del atributo debe coincidir con la siguiente parte del prefijo
    // (ttl)
    private Map<String, Duration> ttl;
}
