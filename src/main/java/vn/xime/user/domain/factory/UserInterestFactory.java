package vn.xime.user.domain.factory;

import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.UserInterest;

public class UserInterestFactory {

    public UserInterest create(
            Id userId,
            Id interestId,
            double weight
    ) {
        // =========================
        // VALIDATE
        // =========================

        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        if (interestId == null) {
            throw new IllegalArgumentException("interestId is required");
        }

        if (weight < 0) {
            throw new IllegalArgumentException("weight must be >= 0");
        }

        // =========================
        // NORMALIZE
        // =========================

        double normalizedWeight = normalize(weight);

        // =========================
        // BUILD
        // =========================

        Id id = IdFactory.generate();

        return new UserInterest(
                id,
                userId,
                interestId,
                normalizedWeight
        );
    }

    private double normalize(double weight) {
        if (weight > 1.0) {
            return 1.0;
        }
        return weight;
    }
}