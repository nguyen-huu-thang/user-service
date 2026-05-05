package vn.xime.user.domain.repository;

import vn.xime.user.domain.model.ContactType;
import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.UserContact;

import java.util.List;
import java.util.Optional;

public interface UserContactRepository {

    UserContact save(UserContact contact);

    Optional<UserContact> findById(Id id);

    List<UserContact> findByUserId(Id userId);

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