package vn.xime.user.domain.factory;

import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.Interest;

public class InterestFactory {

    public Interest create(String name) {
        // =========================
        // VALIDATE
        // =========================

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }

        if (name.length() > 100) {
            throw new IllegalArgumentException("name too long");
        }

        // =========================
        // NORMALIZE
        // =========================

        String normalized = normalize(name);

        // =========================
        // BUILD
        // =========================

        Id id = IdFactory.generate();

        return new Interest(
                id,
                normalized
        );
    }

    private String normalize(String name) {
        return name.trim();
    }
}