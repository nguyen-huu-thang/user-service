package vn.xime.user.application.port.out.crypto;

/**
 * =========================================================
 * PASSWORD HASHER
 * =========================================================
 *
 * Abstraction cho password hashing.
 *
 * =========================================================
 * RESPONSIBILITY
 * =========================================================
 *
 * Interface này chịu trách nhiệm:
 *
 * - hash raw password
 * - verify raw password với stored hash
 *
 * =========================================================
 * IMPORTANT
 * =========================================================
 *
 * Đây là:
 *
 * - PASSWORD HASHING
 *
 * KHÔNG phải:
 *
 * - reversible encryption
 * - encoding
 *
 * Password hash phải là:
 *
 * - one-way
 * - non-reversible
 * - slow hash
 * - resistant against brute-force attack
 *
 * =========================================================
 * RECOMMENDED ALGORITHMS
 * =========================================================
 *
 * Nên dùng:
 *
 * - Argon2id
 * - BCrypt
 * - scrypt
 *
 * KHÔNG dùng:
 *
 * - MD5
 * - SHA1
 * - SHA256 trực tiếp
 *
 * vì các thuật toán này quá nhanh cho password hashing.
 *
 * =========================================================
 * IMPLEMENTATION NOTES
 * =========================================================
 *
 * Infrastructure implementation nên:
 *
 * - tự generate salt
 * - embed algorithm metadata trong hash
 * - support future algorithm migration
 * - support configurable cost factor
 *
 * =========================================================
 * SECURITY NOTES
 * =========================================================
 *
 * Raw password:
 *
 * - KHÔNG được log
 * - KHÔNG được persist
 * - KHÔNG được cache
 *
 * Hash:
 *
 * - có thể persist
 * - dùng để verify password sau này
 *
 * =========================================================
 * EXAMPLE
 * =========================================================
 *
 * hash:
 *
 * raw password
 *      ↓
 * PasswordHasher.hash(...)
 *      ↓
 * stored password hash
 *
 * verify:
 *
 * raw password
 * +
 * stored hash
 *      ↓
 * PasswordHasher.matches(...)
 *      ↓
 * true / false
 *
 * =========================================================
 */
public interface PasswordHasher {

    /**
     * =====================================================
     * HASH PASSWORD
     * =====================================================
     *
     * Hash raw password thành secure one-way hash.
     *
     * =====================================================
     * INPUT
     * =====================================================
     *
     * rawPassword:
     *
     * - raw plaintext password
     * - chưa hash
     * - chưa encrypt
     *
     * =====================================================
     * RETURN
     * =====================================================
     *
     * Password hash đã sẵn sàng để persist.
     *
     * =====================================================
     * NOTE
     * =====================================================
     *
     * Implementation nên:
     *
     * - tự generate salt
     * - tự embed metadata nếu cần
     *
     * =====================================================
     */
    String hash(String rawPassword);


    /**
     * =====================================================
     * VERIFY PASSWORD
     * =====================================================
     *
     * Verify raw password với stored password hash.
     *
     * =====================================================
     * RETURN
     * =====================================================
     *
     * true:
     * - password hợp lệ
     *
     * false:
     * - password không khớp
     *
     * =====================================================
     * NOTE
     * =====================================================
     *
     * Implementation phải:
     *
     * - hash raw password theo cùng algorithm
     * - compare an toàn
     * - tránh timing attack nếu có thể
     *
     * =====================================================
     */
    boolean matches(
        String rawPassword,
        String passwordHash
    );
}