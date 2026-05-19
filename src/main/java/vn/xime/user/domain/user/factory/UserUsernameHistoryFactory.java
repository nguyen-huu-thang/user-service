package vn.xime.user.domain.user.factory;

import vn.xime.user.domain.sharedkernel.factory.IdFactory;
import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.user.model.UserUsernameHistory;

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