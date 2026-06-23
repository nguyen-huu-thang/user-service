package vn.xime.user.domain.user.service;

import org.junit.jupiter.api.Test;

import vn.xime.user.domain.authentication.model.IdentifierType;
import vn.xime.user.domain.contact.model.ContactType;
import vn.xime.user.domain.user.factory.UserContactFactory;
import vn.xime.user.domain.user.factory.UserFactory;
import vn.xime.user.domain.user.service.RegistrationDomainService.RegistrationResult;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationDomainServiceTest {

    private final RegistrationDomainService service =
            new RegistrationDomainService(
                    new UserFactory(),
                    new UserContactFactory()
            );

    @Test
    void username_registration_sets_username_and_no_contact() {
        RegistrationResult result = service.register(
                IdentifierType.USERNAME, "alice", "hash");

        assertThat(result.user().getUsername()).isEqualTo("alice");
        assertThat(result.contact()).isNull();
    }

    @Test
    void email_registration_has_null_username_and_primary_email_contact() {
        RegistrationResult result = service.register(
                IdentifierType.EMAIL, "alice@example.com", "hash");

        assertThat(result.user().getUsername()).isNull();
        assertThat(result.contact()).isNotNull();
        assertThat(result.contact().getType()).isEqualTo(ContactType.EMAIL);
        assertThat(result.contact().getValue()).isEqualTo("alice@example.com");
        assertThat(result.contact().isPrimary()).isTrue();
        assertThat(result.contact().isVerified()).isFalse();
        // contact phải gắn đúng user vừa tạo
        assertThat(result.contact().getUserId()).isEqualTo(result.user().getId());
    }

    @Test
    void phone_registration_has_null_username_and_primary_phone_contact() {
        RegistrationResult result = service.register(
                IdentifierType.PHONE, "0900000000", "hash");

        assertThat(result.user().getUsername()).isNull();
        assertThat(result.contact().getType()).isEqualTo(ContactType.PHONE);
        assertThat(result.contact().isPrimary()).isTrue();
    }
}
