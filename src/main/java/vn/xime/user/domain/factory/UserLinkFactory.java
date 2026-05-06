package vn.xime.user.domain.factory;

import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.LinkType;
import vn.xime.user.domain.model.UserLink;

public class UserLinkFactory {

    public UserLink create(
            Id userId,
            LinkType type,
            String url
    ) {
        // =========================
        // VALIDATE
        // =========================

        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        if (type == null) {
            throw new IllegalArgumentException("type is required");
        }

        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("url is required");
        }

        // =========================
        // NORMALIZE
        // =========================

        String normalized = normalize(url);

        // =========================
        // BUILD
        // =========================

        Id id = IdFactory.generate();

        return new UserLink(
                id,
                userId,
                type,
                normalized
        );
    }

    private String normalize(String url) {
        return url.trim();
    }
}