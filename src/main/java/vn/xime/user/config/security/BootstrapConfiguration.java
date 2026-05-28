package vn.xime.user.config.security;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

import vn.xime.user.infrastructure.security.bootstrap.Bootstrap;
import vn.xime.user.infrastructure.security.bootstrap.BootstrapLoader;
import vn.xime.user.infrastructure.security.bootstrap.BootstrapValidator;


/**
 * =========================================================
 * BOOTSTRAP CONFIGURATION
 * =========================================================
 *
 * Responsibility:
 *
 * - load bootstrap configuration
 * - create bootstrap beans
 * - override bootstrap defaults
 *
 * =========================================================
 */
@Configuration
@EnableConfigurationProperties(
    BootstrapConfiguration.BootstrapProperties.class
)
public class BootstrapConfiguration {

    // =====================================================
    // BOOTSTRAP LOADER
    // =====================================================

    @Bean
    BootstrapLoader bootstrapLoader() {

        return new BootstrapLoader();
    }

    // =====================================================
    // BOOTSTRAP VALIDATOR
    // =====================================================

    @Bean
    BootstrapValidator bootstrapValidator() {

        return new BootstrapValidator();
    }

    // =====================================================
    // BOOTSTRAP
    // =====================================================

    @Bean
    Bootstrap bootstrap(
        BootstrapProperties properties,
        BootstrapLoader loader,
        BootstrapValidator validator
    ) {

        return new Bootstrap(

            properties.getServiceId(),

            Path.of(
                properties.getPath()
            ),

            loader,

            validator
        );
    }

    // =====================================================
    // PROPERTIES
    // =====================================================

    @Getter
    @Setter
    @ConfigurationProperties(
        prefix = "user.bootstrap"
    )
    public static class BootstrapProperties {

        /**
         * Default:
         * user-service
         */
        private String serviceId = "user-service";

        /**
         * Default:
         * ./user/runtime/security/bootstrap.txt
         */
        private String path = "./user/runtime/security/bootstrap.txt";
    }
}