package vn.xime.user.domain.service;

import vn.xime.user.domain.model.LinkType;
import vn.xime.user.domain.model.UserLink;

import java.util.List;

public class UserLinkValidationService {

    // =========================
    // BASIC VALIDATION
    // =========================

    public void validateNewLink(
            LinkType type,
            String url
    ) {
        if (type == null) {
            throw new IllegalArgumentException("type is required");
        }

        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("url is required");
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("invalid url format");
        }
    }

    // =========================
    // DUPLICATE CHECK
    // =========================

    public void ensureNoDuplicate(
            List<UserLink> links,
            LinkType type,
            String url
    ) {
        boolean exists = links.stream()
                .anyMatch(l ->
                        l.getType() == type &&
                        l.getUrl().equalsIgnoreCase(url)
                );

        if (exists) {
            throw new IllegalStateException("Link already exists");
        }
    }

    // =========================
    // SOCIAL RULE (OPTIONAL)
    // =========================

    public void validateSocialLink(
            LinkType type,
            String url
    ) {
        if (type == null) return;

        switch (type) {
            case FACEBOOK -> {
                if (!url.contains("facebook.com")) {
                    throw new IllegalArgumentException("invalid facebook url");
                }
            }
            case TWITTER -> {
                if (!url.contains("twitter.com") && !url.contains("x.com")) {
                    throw new IllegalArgumentException("invalid twitter url");
                }
            }
            case GITHUB -> {
                if (!url.contains("github.com")) {
                    throw new IllegalArgumentException("invalid github url");
                }
            }
        }
    }

    // =========================
    // LIMIT RULE (OPTIONAL)
    // =========================

    public void ensureMaxLinks(
            List<UserLink> links,
            int max
    ) {
        if (links.size() >= max) {
            throw new IllegalStateException("Too many links");
        }
    }

    // =========================
    // EXISTENCE
    // =========================

    public void ensureLinkExists(UserLink link) {
        if (link == null) {
            throw new IllegalArgumentException("UserLink is required");
        }
    }
}