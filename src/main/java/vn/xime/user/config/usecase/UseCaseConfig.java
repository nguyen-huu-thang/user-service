package vn.xime.user.config.usecase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.xime.user.domain.authentication.service.IdentifierNormalizer;
import vn.xime.user.domain.user.factory.UserContactFactory;
import vn.xime.user.domain.user.factory.UserAddressFactory;
import vn.xime.user.domain.user.factory.UserLinkFactory;
import vn.xime.user.domain.user.factory.UserInterestFactory;
import vn.xime.user.domain.user.factory.UserFactory;
import vn.xime.user.domain.user.policy.PasswordPolicy;
import vn.xime.user.domain.user.service.RegistrationDomainService;
import vn.xime.user.domain.contact.service.UserContactDomainService;




@Configuration
public class UseCaseConfig {

    @Bean
    public IdentifierNormalizer identifierNormalizer() {
        return new IdentifierNormalizer();
    }

    // =========================
    // DOMAIN FACTORIES
    // =========================
    // Wire factory thuần (pure domain) thành bean để use case
    // tạo aggregate qua factory thay vì new trực tiếp -
    // giữ normalization + validation ở đúng tầng domain.

    @Bean
    public UserFactory userFactory() {
        return new UserFactory();
    }

    @Bean
    public UserContactFactory userContactFactory() {
        return new UserContactFactory();
    }

    @Bean
    public UserAddressFactory userAddressFactory() {
        return new UserAddressFactory();
    }

    @Bean
    public UserLinkFactory userLinkFactory() {
        return new UserLinkFactory();
    }

    @Bean
    public UserInterestFactory userInterestFactory() {
        return new UserInterestFactory();
    }

    // =========================
    // DOMAIN SERVICES
    // =========================

    @Bean
    public UserContactDomainService userContactDomainService() {
        return new UserContactDomainService();
    }

    @Bean
    public PasswordPolicy passwordPolicy() {
        return new PasswordPolicy();
    }

    @Bean
    public RegistrationDomainService registrationDomainService(
            UserFactory userFactory,
            UserContactFactory userContactFactory
    ) {
        return new RegistrationDomainService(userFactory, userContactFactory);
    }
}