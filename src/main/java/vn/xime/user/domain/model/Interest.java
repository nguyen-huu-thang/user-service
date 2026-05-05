package vn.xime.user.domain.model;

import java.util.Objects;

public class Interest {

    private final Id id;
    private final String name;

    public Interest(
            Id id,
            String name
    ) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);

        validate();
    }

    // =========================
    // VALIDATION
    // =========================

    private void validate() {
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }

        if (name.length() > 100) {
            throw new IllegalArgumentException("name too long");
        }
    }

    // =========================
    // BUSINESS LOGIC
    // =========================

    public boolean sameName(String other) {
        return name.equalsIgnoreCase(other);
    }

    // =========================
    // STATE CHANGE
    // =========================

    public Interest rename(String newName) {
        return new Interest(
                id,
                newName
        );
    }

    // =========================
    // GETTERS
    // =========================

    public Id getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}