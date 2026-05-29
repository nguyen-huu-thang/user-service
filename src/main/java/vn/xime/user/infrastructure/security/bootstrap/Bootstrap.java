package vn.xime.user.infrastructure.security.bootstrap;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Objects;

import vn.xime.user.integration.trust.model.Certificate;


/**
 * =========================================================
 * BOOTSTRAP
 * =========================================================
 *
 * Responsible for:
 *
 * - bootstrap file access
 * - bootstrap loading
 * - bootstrap validation
 * - bootstrap certificate conversion
 * - bootstrap file deletion
 *
 * Bootstrap is used only for:
 *
 * - first startup
 * - trust establishment
 * - initial runtime certificate exchange
 *
 * Bootstrap is NOT:
 *
 * - runtime certificate storage
 * - runtime identity source
 * - long-term trust storage
 *
 * =========================================================
 */
public class Bootstrap {

    // =====================================================
    // DEFAULTS
    // =====================================================

    public static final String DEFAULT_SERVICE_ID = "user-service";

    public static final Path DEFAULT_BOOTSTRAP_PATH =
        Path.of(
            "./runtime/security/bootstrap.txt"
        );

    // =====================================================
    // FIELDS
    // =====================================================

    private final String currentServiceId;

    private final Path bootstrapPath;

    private final BootstrapLoader loader;

    private final BootstrapValidator validator;

    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public Bootstrap() {

        this(
            DEFAULT_SERVICE_ID,
            DEFAULT_BOOTSTRAP_PATH
        );
    }

    public Bootstrap(
        String currentServiceId,
        Path bootstrapPath
    ) {

        this(
            currentServiceId,
            bootstrapPath,
            new BootstrapLoader(),
            new BootstrapValidator()
        );
    }

    public Bootstrap(
        String currentServiceId,
        Path bootstrapPath,
        BootstrapLoader loader,
        BootstrapValidator validator
    ) {

        this.currentServiceId =
            Objects.requireNonNull(
                currentServiceId,
                "currentServiceId must not be null"
            );

        this.bootstrapPath =
            Objects.requireNonNull(
                bootstrapPath,
                "bootstrapPath must not be null"
            );

        this.loader =
            Objects.requireNonNull(
                loader,
                "loader must not be null"
            );

        this.validator =
            Objects.requireNonNull(
                validator,
                "validator must not be null"
            );
    }

    // =====================================================
    // EXISTS
    // =====================================================

    public boolean exists() {

        return Files.isRegularFile(
            bootstrapPath
        ) && Files.isReadable(
            bootstrapPath
        );
    }

    // =====================================================
    // LOAD
    // =====================================================

    public Certificate load() {

        BootstrapPayload payload =
            loader.load(
                bootstrapPath
            );

        validator.validate(
            currentServiceId,
            payload
        );

        return mapCertificate(
            payload
        );
    }

    // =====================================================
    // DELETE
    // =====================================================

    public void delete() {

        try {

            Files.deleteIfExists(
                bootstrapPath
            );

        } catch (IOException e) {

            throw new IllegalStateException(
                """
                Failed to delete bootstrap file.

                Path:
                %s
                """.formatted(
                    bootstrapPath
                ),
                e
            );
        }
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public String currentServiceId() {

        return currentServiceId;
    }

    public Path bootstrapPath() {

        return bootstrapPath;
    }

    // =====================================================
    // MAPPER
    // =====================================================

    private Certificate mapCertificate(
        BootstrapPayload payload
    ) {

        BootstrapPayload.Certificate cert =
            payload.certificate();

        return new Certificate(

            cert.id(),

            cert.publicCert(),

            cert.privateKey(),

            cert.serviceId(),

            payload.tokenId(),

            payload.refreshToken(),

            cert.issuedAtInstant(),

            cert.expiresAtInstant()
        );
    }
}
