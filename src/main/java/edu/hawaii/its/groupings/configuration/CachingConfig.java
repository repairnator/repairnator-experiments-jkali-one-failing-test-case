package edu.hawaii.its.groupings.configuration;

import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache("default"),
                new ConcurrentMapCache("campusesAll"),
                new ConcurrentMapCache("campusesActualAll"),
                new ConcurrentMapCache("campusesById"),
                new ConcurrentMapCache("holidays"),
                new ConcurrentMapCache("holidaysById"),
                new ConcurrentMapCache("holidaysByYear"),
                new ConcurrentMapCache("holidayTypes"),
                new ConcurrentMapCache("holidayTypesById"),
                new ConcurrentMapCache("messages")));
        return cacheManager;
    }

}
