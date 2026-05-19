package vn.xime.user.domain.sharedkernel.service;

import java.util.Arrays;

import vn.xime.user.domain.sharedkernel.model.Id;

public final class IdService {

    private static final char[] BASE62 =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    private static final int[] BASE62_INDEX = new int[128];

    // trust-service
    private static final int ID_LENGTH_20 = 20;
    private static final int BASE62_LENGTH_20 = 27;

    // other services
    private static final int ID_LENGTH_24 = 24;
    private static final int BASE62_LENGTH_24 = 33;

    private static final char[] HEX = "0123456789abcdef".toCharArray();

    static {
        Arrays.fill(BASE62_INDEX, -1);
        for (int i = 0; i < BASE62.length; i++) {
            BASE62_INDEX[BASE62[i]] = i;
        }
    }

    private IdService() {}

    // =========================
    // PUBLIC API
    // =========================

    public static String toString(Id id) {
        return toBase62(id);
    }

    public static String toBase62(Id id) {
        byte[] bytes = id.toBytes();

        String encoded = encodeBase62(bytes);

        if (bytes.length == ID_LENGTH_20) {
            return leftPad(encoded, BASE62_LENGTH_20, '0');
        }

        if (bytes.length == ID_LENGTH_24) {
            return leftPad(encoded, BASE62_LENGTH_24, '0');
        }

        throw new IllegalArgumentException(
                "Unsupported Id length: " + bytes.length
        );
    }

    public static Id fromString(String value) {

        if (value == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        final int targetLength;

        if (value.length() == BASE62_LENGTH_20) {
            targetLength = ID_LENGTH_20;
        }
        else if (value.length() == BASE62_LENGTH_24) {
            targetLength = ID_LENGTH_24;
        }
        else {
            throw new IllegalArgumentException(
                    "Invalid Base62 length: " + value.length()
            );
        }

        byte[] decoded = decodeBase62(value);

        if (decoded.length > targetLength) {
            throw new IllegalArgumentException("Invalid Id length");
        }

        byte[] result = new byte[targetLength];

        System.arraycopy(
                decoded,
                0,
                result,
                targetLength - decoded.length,
                decoded.length
        );

        return new Id(result);
    }

    public static String toHex(Id id) {
        byte[] bytes = id.toBytes();
        char[] hex = new char[bytes.length * 2];

        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hex[i * 2] = HEX[v >>> 4];
            hex[i * 2 + 1] = HEX[v & 0x0F];
        }

        return new String(hex);
    }

    public static String toByteString(Id id) {
        return Arrays.toString(id.toBytes());
    }

    public static long extractTimestampSeconds(Id id) {
        byte[] bytes = id.toBytes();

        return ((bytes[0] & 0xFFL) << 24) |
               ((bytes[1] & 0xFFL) << 16) |
               ((bytes[2] & 0xFFL) << 8)  |
               (bytes[3] & 0xFFL);
    }

    public static java.time.Instant extractInstant(Id id) {
        return java.time.Instant.ofEpochSecond(extractTimestampSeconds(id));
    }

    public static boolean sameSecond(Id a, Id b) {
        byte[] x = a.toBytes();
        byte[] y = b.toBytes();

        return x[0] == y[0] &&
               x[1] == y[1] &&
               x[2] == y[2] &&
               x[3] == y[3];
    }

    // =========================
    // BASE62 ENCODE
    // =========================

    private static String encodeBase62(byte[] input) {
        byte[] number = Arrays.copyOf(input, input.length);
        char[] buffer = new char[64];
        int pos = buffer.length;

        while (!isZero(number)) {
            int remainder = divmod(number, 62);
            buffer[--pos] = BASE62[remainder];
        }

        return pos == buffer.length
                ? "0"
                : new String(buffer, pos, buffer.length - pos);
    }

    // =========================
    // BASE62 DECODE
    // =========================

    private static byte[] decodeBase62(String input) {
        byte[] result = new byte[0];

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c >= 128 || BASE62_INDEX[c] == -1) {
                throw new IllegalArgumentException(
                        "Invalid Base62 character: " + c
                );
            }

            int value = BASE62_INDEX[c];
            result = multiplyAndAdd(result, 62, value);
        }

        return result;
    }

    // =========================
    // CORE MATH
    // =========================

    private static int divmod(byte[] number, int divisor) {
        int remainder = 0;

        for (int i = 0; i < number.length; i++) {
            int digit = number[i] & 0xFF;
            int temp = remainder * 256 + digit;

            number[i] = (byte) (temp / divisor);
            remainder = temp % divisor;
        }

        return remainder;
    }

    private static byte[] multiplyAndAdd(byte[] number, int base, int addition) {

        if (number.length == 0) {
            return new byte[]{(byte) addition};
        }

        int carry = addition;
        byte[] result = new byte[number.length];

        for (int i = number.length - 1; i >= 0; i--) {
            int value = (number[i] & 0xFF) * base + carry;
            result[i] = (byte) value;
            carry = value >>> 8;
        }

        if (carry == 0) {
            return result;
        }

        int extraBytes = 0;
        int tmp = carry;

        while (tmp > 0) {
            extraBytes++;
            tmp >>>= 8;
        }

        byte[] expanded = new byte[result.length + extraBytes];

        System.arraycopy(result, 0, expanded, extraBytes, result.length);

        for (int i = extraBytes - 1; i >= 0; i--) {
            expanded[i] = (byte) (carry & 0xFF);
            carry >>>= 8;
        }

        return expanded;
    }

    private static boolean isZero(byte[] number) {
        for (byte b : number) {
            if (b != 0) {
                return false;
            }
        }

        return true;
    }

    // =========================
    // UTIL
    // =========================

    private static String leftPad(
            String input,
            int length,
            char padChar
    ) {

        if (input.length() > length) {
            throw new IllegalStateException("Base62 overflow");
        }

        if (input.length() == length) {
            return input;
        }

        char[] result = new char[length];
        int padLength = length - input.length();

        for (int i = 0; i < padLength; i++) {
            result[i] = padChar;
        }

        input.getChars(0, input.length(), result, padLength);

        return new String(result);
    }
}