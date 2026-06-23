package vn.xime.user.domain.contact.service;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import vn.xime.user.domain.contact.model.ContactType;
import vn.xime.user.domain.contact.model.UserContact;
import vn.xime.user.domain.contact.service.UserContactDomainService.PrimaryAssignment;
import vn.xime.user.domain.sharedkernel.factory.IdFactory;
import vn.xime.user.domain.sharedkernel.model.Id;

import static org.assertj.core.api.Assertions.assertThat;

class UserContactDomainServiceTest {

    private final UserContactDomainService service = new UserContactDomainService();

    private final Id userId = IdFactory.generate();

    private UserContact contact(boolean primary) {
        return new UserContact(
                IdFactory.generate(),
                userId,
                ContactType.EMAIL,
                "a@example.com",
                true,
                primary,
                Instant.now()
        );
    }

    @Test
    void promotes_target_and_no_demotion_when_no_current_primary() {
        UserContact target = contact(false);

        PrimaryAssignment result = service.setPrimary(target, Optional.empty());

        assertThat(result.getPromoted().isPrimary()).isTrue();
        assertThat(result.getDemoted()).isEmpty();
    }

    @Test
    void promotes_target_and_demotes_existing_different_primary() {
        UserContact current = contact(true);
        UserContact target = contact(false);

        PrimaryAssignment result = service.setPrimary(target, Optional.of(current));

        assertThat(result.getPromoted().isPrimary()).isTrue();
        assertThat(result.getDemoted()).isPresent();
        assertThat(result.getDemoted().get().isPrimary()).isFalse();
    }

    @Test
    void does_not_demote_when_current_primary_is_the_target() {
        UserContact target = contact(true);

        PrimaryAssignment result = service.setPrimary(target, Optional.of(target));

        assertThat(result.getPromoted().isPrimary()).isTrue();
        assertThat(result.getDemoted()).isEmpty();
    }
}
