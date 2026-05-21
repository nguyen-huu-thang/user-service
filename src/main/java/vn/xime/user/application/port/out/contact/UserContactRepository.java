package vn.xime.user.application.port.out.contact;

import vn.xime.user.domain.contact.model.ContactType;
import vn.xime.user.domain.contact.model.UserContact;
import vn.xime.user.domain.sharedkernel.model.Id;

import java.util.List;
import java.util.Optional;

public interface UserContactRepository {

    UserContact save(UserContact contact);

    Optional<UserContact> findById(Id id);

    List<UserContact> findByUserId(Id userId);

    boolean existsByTypeAndValue(
        ContactType type,
        String value
    );

    // =========================
    // TYPE
    // =========================

    List<UserContact> findByUserIdAndType(Id userId, ContactType type);

    // =========================
    // VERIFIED
    // =========================

    List<UserContact> findVerifiedContacts(Id userId);

    List<UserContact> findUnverifiedContacts(Id userId);

    // =========================
    // PRIMARY
    // =========================

    Optional<UserContact> findPrimaryContact(Id userId, ContactType type);

    // =========================
    // VALUE (lookup nhanh)
    // =========================

    Optional<UserContact> findByTypeAndValue(ContactType type, String value);

    // =========================
    // DELETE
    // =========================

    boolean deleteById(Id id);

    void deleteAllByUserId(Id userId);
}