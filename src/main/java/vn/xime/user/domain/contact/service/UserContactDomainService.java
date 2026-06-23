package vn.xime.user.domain.contact.service;

import java.util.Optional;

import vn.xime.user.domain.contact.model.UserContact;

/**
 * =========================================================
 * USER CONTACT DOMAIN SERVICE
 * =========================================================
 *
 * Pure domain service - chứa các quy tắc nghiệp vụ liên quan
 * tới NHIỀU UserContact (cross-entity rule), không thuộc về
 * riêng một aggregate instance nào.
 *
 * Không phụ thuộc framework, repository hay I/O - chỉ nhận
 * domain object vào, trả domain object ra. Việc load/save do
 * tầng application đảm nhiệm.
 */
public class UserContactDomainService {

    /**
     * =====================================================
     * SET PRIMARY
     * =====================================================
     *
     * Invariant: mỗi (user, type) chỉ có tối đa 1 primary.
     *
     * Khi promote một contact lên primary thì primary hiện tại
     * (nếu khác) phải bị demote. Đây là quy tắc liên quan 2
     * contact nên đặt ở domain service, không ở use case.
     *
     * @param target         contact muốn đặt làm primary
     * @param currentPrimary primary hiện tại cùng type (nếu có)
     * @return cặp contact đã thay đổi cần được lưu lại
     */
    public PrimaryAssignment setPrimary(
            UserContact target,
            Optional<UserContact> currentPrimary
    ) {
        if (target == null) {
            throw new IllegalArgumentException("target contact is required");
        }

        UserContact promoted = target.markPrimary();

        // Demote primary cũ, trừ khi nó chính là target.
        UserContact demoted = currentPrimary
                .filter(existing -> !existing.getId().equals(target.getId()))
                .map(UserContact::unmarkPrimary)
                .orElse(null);

        return new PrimaryAssignment(promoted, demoted);
    }

    /**
     * =====================================================
     * PRIMARY ASSIGNMENT RESULT
     * =====================================================
     *
     * Kết quả của thao tác set-primary: contact được promote và
     * contact bị demote (có thể không có).
     */
    public static final class PrimaryAssignment {

        private final UserContact promoted;
        private final UserContact demoted; // nullable

        public PrimaryAssignment(UserContact promoted, UserContact demoted) {
            this.promoted = promoted;
            this.demoted = demoted;
        }

        public UserContact getPromoted() {
            return promoted;
        }

        public Optional<UserContact> getDemoted() {
            return Optional.ofNullable(demoted);
        }
    }
}
