package vn.xime.user.application.dto.external.history;

import java.time.Instant;

public record UsernameHistoryResponse(

    String oldUsername,

    Instant changedAt
) {
}
