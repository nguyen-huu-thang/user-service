package vn.xime.user.domain.service;

import vn.xime.user.domain.model.UserInterest;

import java.util.List;

public class UserInterestValidationService {

    // =========================
    // BASIC VALIDATION
    // =========================

    public void validateNew(
            double weight
    ) {
        if (weight < 0) {
            throw new IllegalArgumentException("weight must be >= 0");
        }
    }

    // =========================
    // DUPLICATE RULE
    // =========================

    /**
     * mỗi user chỉ có 1 record cho 1 interest
     */
    public void ensureNoDuplicate(
            List<UserInterest> existing,
            UserInterest newInterest
    ) {
        boolean exists = existing.stream()
                .anyMatch(i ->
                        i.getInterestId().equals(newInterest.getInterestId())
                );

        if (exists) {
            throw new IllegalStateException("User already has this interest");
        }
    }

    // =========================
    // WEIGHT RULE
    // =========================

    public void validateWeight(double weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("weight must be >= 0");
        }

        if (weight > 1.0) {
            throw new IllegalArgumentException("weight must be <= 1.0");
        }
    }

    // =========================
    // BUSINESS RULE (OPTIONAL)
    // =========================

    /**
     * giới hạn tổng số interest
     */
    public void ensureMaxInterests(
            List<UserInterest> interests,
            int max
    ) {
        if (interests.size() >= max) {
            throw new IllegalStateException("Too many interests");
        }
    }

    /**
     * đảm bảo tổng weight không vượt quá 1 (nếu bạn dùng normalized distribution)
     */
    public void ensureTotalWeight(
            List<UserInterest> interests
    ) {
        double sum = interests.stream()
                .mapToDouble(UserInterest::getWeight)
                .sum();

        if (sum > 1.0) {
            throw new IllegalStateException("Total weight exceeds 1.0");
        }
    }
}