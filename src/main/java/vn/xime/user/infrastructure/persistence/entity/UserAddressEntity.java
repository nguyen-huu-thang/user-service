package vn.xime.user.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "user_addresses",
        indexes = {
                @Index(name = "idx_user_addresses_id", columnList = "id"),
                @Index(name = "idx_user_addresses_user_id", columnList = "user_id"),
                @Index(name = "idx_user_addresses_type", columnList = "type"),
                @Index(name = "idx_user_addresses_time", columnList = "user_id,start_date,end_date")
        }
)
public class UserAddressEntity {

    // =========================
    // ID (KSUID - BYTEA)
    // =========================

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BYTEA")
    private byte[] id;

    // =========================
    // RELATIONSHIP
    // =========================

    @Column(name = "user_id", nullable = false, columnDefinition = "BYTEA")
    private byte[] userId;

    // =========================
    // TYPE
    // =========================

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    // =========================
    // ADDRESS INFO
    // =========================

    @Column(name = "country", length = 10)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "region", length = 100)
    private String region;

    @Column(name = "address_line", columnDefinition = "TEXT")
    private String addressLine;

    // =========================
    // GEO
    // =========================

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lng")
    private Double lng;

    // =========================
    // TIME RANGE
    // =========================

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    // =========================
    // GETTER / SETTER
    // =========================

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public byte[] getUserId() {
        return userId;
    }

    public void setUserId(byte[] userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }
}