package vn.xime.user.domain.interest.model;

import java.util.Objects;

import vn.xime.user.domain.sharedkernel.model.Id;

public class UserInterest {

    private final Id id;

    private final Id userId;
    private final Id interestId;

    private final double weight;

    public UserInterest(
            Id id,
            Id userId,
            Id interestId,
            double weight
    ) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.interestId = Objects.requireNonNull(interestId);
        this.weight = weight;

        validate();
    }

    // =========================
    // VALIDATION
    // =========================

    private void validate() {
        if (weight < 0) {
            throw new IllegalArgumentException("weight must be >= 0");
        }
    }

    // =========================
    // BUSINESS LOGIC
    // =========================

    public boolean isStrongInterest() {
        return weight >= 0.7;
    }

    public boolean isWeakInterest() {
        return weight < 0.3;
    }

    // =========================
    // STATE CHANGE
    // =========================

    public UserInterest updateWeight(double newWeight) {
        return new UserInterest(
                id,
                userId,
                interestId,
                newWeight
        );
    }

    public UserInterest increaseWeight(double delta) {
        return new UserInterest(
                id,
                userId,
                interestId,
                this.weight + delta
        );
    }

    public UserInterest decreaseWeight(double delta) {
        double newWeight = this.weight - delta;
        if (newWeight < 0) {
            newWeight = 0;
        }

        return new UserInterest(
                id,
                userId,
                interestId,
                newWeight
        );
    }

    // =========================
    // GETTERS
    // =========================

    public Id getId() {
        return id;
    }

    public Id getUserId() {
        return userId;
    }

    public Id getInterestId() {
        return interestId;
    }

    public double getWeight() {
        return weight;
    }
}