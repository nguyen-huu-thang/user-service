package vn.xime.user.infrastructure.security.store;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Component;


/**
 * =========================================================
 * ROOT CERTIFICATE FILE STORE
 * =========================================================
 *
 * Filesystem persistence adapter.
 *
 * Responsibilities:
 *
 * - load root certificate from file
 * - save root certificate to file
 * - delete root certificate file
 *
 * KHÔNG:
 *
 * - cache logic
 * - grpc communication
 * - trust orchestration
 * - certificate validation
 *
 * =========================================================
 */
@Component
public class RootCertificateFileStore {

    /**
     * =====================================================
     * ROOT CERTIFICATE PATH
     * =====================================================
     */
    private static final Path ROOT_CERT_PATH =
        Path.of(
            "src/main/resources/security/trust/ca-cert.pem"
        );


    /**
     * =====================================================
     * LOAD ROOT CERTIFICATE
     * =====================================================
     */
    public String load() {

        try {

            return Files.readString(
                ROOT_CERT_PATH
            );

        } catch (IOException exception) {

            throw new RuntimeException(
                "failed to load root certificate",
                exception
            );
        }
    }


    /**
     * =====================================================
     * SAVE ROOT CERTIFICATE
     * =====================================================
     */
    public void save(
        String rootCertificate
    ) {

        try {

            Path parent =
                ROOT_CERT_PATH.getParent();

            if (parent != null) {

                Files.createDirectories(
                    parent
                );
            }

            Files.writeString(
                ROOT_CERT_PATH,
                rootCertificate
            );

        } catch (IOException exception) {

            throw new RuntimeException(
                "failed to save root certificate",
                exception
            );
        }
    }


    /**
     * =====================================================
     * DELETE ROOT CERTIFICATE
     * =====================================================
     */
    public void delete() {

        try {

            Files.deleteIfExists(
                ROOT_CERT_PATH
            );

        } catch (IOException exception) {

            throw new RuntimeException(
                "failed to delete root certificate",
                exception
            );
        }
    }


    /**
     * =====================================================
     * EXISTS
     * =====================================================
     */
    public boolean exists() {

        return Files.exists(
            ROOT_CERT_PATH
        );
    }
}