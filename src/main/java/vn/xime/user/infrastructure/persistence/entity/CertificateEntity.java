package vn.xime.user.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;


@Entity
@Table(
    name = "certificates",

    indexes = {

        // =========================
        // SERVICE LOOKUP
        // =========================

        @Index(
            name = "idx_certificates_service_id",
            columnList = "service_id"
        ),

        // =========================
        // LIFECYCLE LOOKUP
        // =========================

        @Index(
            name = "idx_certificates_expires_at",
            columnList = "expires_at"
        ),

        @Index(
            name = "idx_certificates_issued_at",
            columnList = "issued_at"
        )
    }
)
public class CertificateEntity {

    // =========================
    // CERTIFICATE IDENTITY
    // =========================

    /**
     * Certificate identity.
     *
     * ⚠️ IMPORTANT:
     *
     * certificateId được dùng làm PRIMARY KEY
     * thay vì refreshTokenId.
     *
     * Lý do:
     *
     * Trust Service có thể trả:
     *
     * - refresh token mới
     * - refresh token id mới
     *
     * nhưng certificate vẫn giữ nguyên
     * nếu chưa tới thời điểm rotate cert.
     *
     * Nếu dùng refreshTokenId làm primary key:
     *
     * - mỗi lần rotate token
     * - sẽ tạo thêm một row mới
     * - dù certificate material không đổi
     *
     * Điều đó gây:
     *
     * - duplicate PEM certificate
     * - duplicate private key
     * - tăng storage không cần thiết
     * - sai boundary dữ liệu
     *
     * Trong Identity Service:
     *
     * - certificate mới là dữ liệu chính
     * - refresh token chỉ là metadata runtime
     *
     * Vì vậy:
     *
     * - cùng certificateId
     *   => update token mới
     *
     * - certificateId mới
     *   => insert certificate mới
     *
     * Điều này giúp:
     *
     * - chỉ lưu mỗi cert một lần
     * - tránh duplicate cryptographic material
     * - đồng bộ đúng lifecycle của Trust Service
     * - giảm storage
     * - đơn giản cleanup
     */
    @Id
    @Column(
        name = "certificate_id",
        nullable = false,
        length = 100
    )
    private String certificateId;

    /**
     * Service owner của certificate.
     *
     * Ví dụ:
     * - identity-service
     * - user-service
     */
    @Column(
        name = "service_id",
        nullable = false,
        length = 100
    )
    private String serviceId;

    // =========================
    // CERTIFICATE MATERIAL
    // =========================

    /**
     * PEM encoded certificate.
     */
    @Column(
        name = "public_certificate",
        nullable = false,
        columnDefinition = "TEXT"
    )
    private String publicCertificate;

    /**
     * PEM encoded private key.
     *
     * Nên encrypt trước khi lưu DB.
     */
    @Column(
        name = "private_key",
        columnDefinition = "TEXT"
    )
    private String privateKey;

    // =========================
    // REFRESH TOKEN
    // =========================

    /**
     * Current refresh token identity.
     *
     * Có thể thay đổi nhiều lần
     * trong lifecycle của cùng certificate.
     */
    @Column(
        name = "refresh_token_id",
        nullable = false,
        length = 100
    )
    private String refreshTokenId;

    /**
     * Refresh token dùng để rotate certificate.
     *
     * Nên encrypt hoặc hash trước khi lưu DB
     * tùy security model.
     */
    @Column(
        name = "refresh_token",
        columnDefinition = "TEXT"
    )
    private String refreshToken;

    // =========================
    // LIFECYCLE
    // =========================

    @Column(
        name = "issued_at",
        nullable = false
    )
    private Instant issuedAt;

    @Column(
        name = "expires_at",
        nullable = false
    )
    private Instant expiresAt;

    // =========================
    // GETTER / SETTER
    // =========================

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getPublicCertificate() {
        return publicCertificate;
    }

    public void setPublicCertificate(String publicCertificate) {
        this.publicCertificate = publicCertificate;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getRefreshTokenId() {
        return refreshTokenId;
    }

    public void setRefreshTokenId(String refreshTokenId) {
        this.refreshTokenId = refreshTokenId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}