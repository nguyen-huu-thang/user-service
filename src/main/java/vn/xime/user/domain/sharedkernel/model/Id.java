package vn.xime.user.domain.sharedkernel.model;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;

public final class Id {

    public static final int LENGTH_20 = 20;

    public static final int LENGTH_24 = 24;

    private static final int TIMESTAMP_LENGTH = 4;

    private static final long KSUID_EPOCH = 1400000000L;

    private final byte[] value;

    public Id(byte[] value) {

        if (value == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        if (value.length != LENGTH_20 &&
            value.length != LENGTH_24) {

            throw new IllegalArgumentException(
                    "Id must be 20 or 24 bytes"
            );
        }

        this.value = Arrays.copyOf(value, value.length);
    }

    // =========================
    // CORE BEHAVIOR
    // =========================

    public byte[] toBytes() {
        return Arrays.copyOf(value, value.length);
    }

    public int length() {
        return value.length;
    }

    public boolean is20Bytes() {
        return value.length == LENGTH_20;
    }

    public boolean is24Bytes() {
        return value.length == LENGTH_24;
    }

    public Instant getTimestamp() {

        int ts = ByteBuffer
                .wrap(value, 0, TIMESTAMP_LENGTH)
                .getInt();

        long epoch = (ts & 0xFFFFFFFFL) + KSUID_EPOCH;

        return Instant.ofEpochSecond(epoch);
    }

    // =========================
    // EQUALITY
    // =========================

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Id)) {
            return false;
        }

        Id id = (Id) o;

        return Arrays.equals(value, id.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}