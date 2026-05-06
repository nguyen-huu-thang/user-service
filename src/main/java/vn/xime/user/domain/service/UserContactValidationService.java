package vn.xime.user.domain.service;

import vn.xime.user.domain.model.ContactType;
import vn.xime.user.domain.model.UserContact;

import java.util.List;

public class UserContactValidationService {

    // =========================
    // CREATE VALIDATION
    // =========================

    public void validateNewContact(
            ContactType type,
            String value
    ) {
        if (type == null) {
            throw new IllegalArgumentException("type is required");
        }

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("value is required");
        }
    }

    // =========================
    // DUPLICATE CHECK
    // =========================

    public void ensureNoDuplicate(
            List<UserContact> contacts,
            ContactType type,
            String value
    ) {
        boolean exists = contacts.stream()
                .anyMatch(c ->
                        c.getType() == type &&
                        c.getValue().equalsIgnoreCase(value)
                );

        if (exists) {
            throw new IllegalStateException("Contact already exists");
        }
    }

    // =========================
    // PRIMARY RULE
    // =========================

    /**
     * mỗi type chỉ có 1 primary
     */
    public void ensureSinglePrimaryPerType(
            List<UserContact> contacts,
            ContactType type
    ) {
        long count = contacts.stream()
                .filter(c -> c.getType() == type)
                .filter(UserContact::isPrimary)
                .count();

        if (count > 1) {
            throw new IllegalStateException("Multiple primary contacts for same type");
        }
    }

    /**
     * primary phải verified
     */
    public void ensurePrimaryIsVerified(UserContact contact) {
        if (contact.isPrimary() && !contact.isVerified()) {
            throw new IllegalStateException("Primary contact must be verified");
        }
    }

    // =========================
    // STATE VALIDATION
    // =========================

    public void ensureContactExists(UserContact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("UserContact is required");
        }
    }

    // =========================
    // TYPE-SPECIFIC VALIDATION (OPTIONAL UPGRADE)
    // =========================

    public void validateFormat(ContactType type, String value) {
        switch (type) {
            case EMAIL -> {
                if (!value.contains("@")) {
                    throw new IllegalArgumentException("invalid email format");
                }
            }
            case PHONE -> {
                if (value.length() < 6) {
                    throw new IllegalArgumentException("invalid phone number");
                }
            }
        }
    }
}