package vn.xime.user.domain.model;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;

public final class Id {

    private static final int LENGTH = 24;
    private static final int TIMESTAMP_LENGTH = 4;
    private static final long KSUID_EPOCH = 1400000000L;

    private final byte[] value;

    public Id(byte[] value) {
        if (value == null || value.length != LENGTH) {
            throw new IllegalArgumentException("Id must be 24 bytes");
        }
        this.value = Arrays.copyOf(value, LENGTH);
    }

    // =========================
    // CORE BEHAVIOR
    // =========================

    public byte[] toBytes() {
        return Arrays.copyOf(value, LENGTH);
    }

    public Instant getTimestamp() {
        int ts = ByteBuffer.wrap(value, 0, TIMESTAMP_LENGTH).getInt();
        long epoch = (ts & 0xFFFFFFFFL) + KSUID_EPOCH;
        return Instant.ofEpochSecond(epoch);
    }

    // =========================
    // EQUALITY
    // =========================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Id)) return false;
        Id id = (Id) o;
        return Arrays.equals(value, id.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}