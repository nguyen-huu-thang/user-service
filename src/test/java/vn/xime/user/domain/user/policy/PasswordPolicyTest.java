package vn.xime.user.domain.user.policy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordPolicyTest {

    private final PasswordPolicy policy = new PasswordPolicy();

    @Test
    void accepts_password_meeting_min_length() {
        assertThatCode(() -> policy.validate("secret123"))
                .doesNotThrowAnyException();
    }

    @Test
    void rejects_null_password() {
        assertThatThrownBy(() -> policy.validate(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejects_blank_password() {
        assertThatThrownBy(() -> policy.validate("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejects_too_short_password() {
        assertThatThrownBy(() -> policy.validate("12345"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("short");
    }
}
