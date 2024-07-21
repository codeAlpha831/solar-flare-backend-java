package com.solareflarebackend.config;

import org.jodconverter.core.DocumentConverter;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class JodConverterConfig {

    @Bean
    public LocalOfficeManager officeManager() {
        return LocalOfficeManager.install();
    }

    @Bean
    public DocumentConverter documentConverter(LocalOfficeManager officeManager) {
        return LocalConverter.make(officeManager);
    }

    @Bean
    public OfficeManagerLifecycle officeManagerLifecycle(LocalOfficeManager officeManager) {
        return new OfficeManagerLifecycle(officeManager);
    }

    public static class OfficeManagerLifecycle {

        private final LocalOfficeManager officeManager;

        public OfficeManagerLifecycle(LocalOfficeManager officeManager) {
            this.officeManager = officeManager;
        }

        @PostConstruct
        public void start() {
            try {
                officeManager.start();
            } catch (Exception e) {
                throw new RuntimeException("Could not start OfficeManager", e);
            }
        }

        @PreDestroy
        public void stop() {
            try {
                officeManager.stop();
            } catch (Exception e) {
                throw new RuntimeException("Could not stop OfficeManager", e);
            }
        }
    }
}
