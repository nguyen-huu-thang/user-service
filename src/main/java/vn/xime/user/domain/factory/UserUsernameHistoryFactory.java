package vn.xime.user.domain.factory;

import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.UserUsernameHistory;

import java.time.Instant;

public class UserUsernameHistoryFactory {

    public UserUsernameHistory create(
            Id userId,
            String oldUsername
    ) {
        // =========================
        // VALIDATE
        // =========================

        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        if (oldUsername == null || oldUsername.isBlank()) {
            throw new IllegalArgumentException("oldUsername is required");
        }

        // =========================
        // BUILD
        // =========================

        Id id = IdFactory.generate();

        Instant now = Instant.now();

        return new UserUsernameHistory(
                id,
                userId,
                oldUsername,
                now
        );
    }
}