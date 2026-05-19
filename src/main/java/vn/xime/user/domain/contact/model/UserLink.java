package vn.xime.user.domain.contact.model;

import java.util.Objects;

import vn.xime.user.domain.sharedkernel.model.Id;

public class UserLink {

    private final Id id;
    private final Id userId;

    private final LinkType type;
    private final String url;

    public UserLink(
            Id id,
            Id userId,
            LinkType type,
            String url
    ) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.type = Objects.requireNonNull(type);
        this.url = Objects.requireNonNull(url);

        validate();
    }

    // =========================
    // VALIDATION
    // =========================

    private void validate() {
        if (url.isBlank()) {
            throw new IllegalArgumentException("url must not be blank");
        }

        // validation nhẹ
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("url must start with http:// or https://");
        }
    }

    // =========================
    // BUSINESS LOGIC
    // =========================

    public boolean isSocial() {
        return switch (type) {
            case FACEBOOK, TWITTER, GITHUB -> true;
            default -> false;
        };
    }

    public boolean isWebsite() {
        return type == LinkType.WEBSITE;
    }

    // =========================
    // STATE CHANGE
    // =========================

    public UserLink changeUrl(String newUrl) {
        return new UserLink(
                id,
                userId,
                type,
                newUrl
        );
    }

    public UserLink changeType(LinkType newType) {
        return new UserLink(
                id,
                userId,
                newType,
                url
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

    public LinkType getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}