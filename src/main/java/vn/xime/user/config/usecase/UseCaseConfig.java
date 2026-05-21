package vn.xime.user.config.usecase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.xime.user.domain.authentication.service.IdentifierNormalizer;




@Configuration
public class UseCaseConfig {

    @Bean
    public IdentifierNormalizer identifierNormalizer() {
        return new IdentifierNormalizer();
    }
}