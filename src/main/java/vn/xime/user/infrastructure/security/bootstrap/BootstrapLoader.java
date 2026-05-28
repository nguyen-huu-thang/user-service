package vn.xime.user.infrastructure.security.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Base64;


/**
 * =========================================================
 * BOOTSTRAP LOADER
 * =========================================================
 *
 * Responsible for:
 *
 * - reading bootstrap file
 * - base64 decoding
 * - JSON parsing
 * - payload conversion
 *
 * =========================================================
 */
public class BootstrapLoader {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // =========================
    // LOAD
    // =========================

    public BootstrapPayload load(
        Path path
    ) {

        if (path == null) {
            throw new IllegalArgumentException(
                "path must not be null"
            );
        }

        try {

            // =========================
            // READ BASE64 FILE
            // =========================

            String encoded =
                Files.readString(
                    path,
                    StandardCharsets.UTF_8
                );

            // =========================
            // BASE64 DECODE
            // =========================

            byte[] decodedBytes =
                Base64.getDecoder()
                    .decode(
                        encoded.trim()
                    );

            // =========================
            // JSON STRING
            // =========================

            String json =
                new String(
                    decodedBytes,
                    StandardCharsets.UTF_8
                );

            // =========================
            // JSON -> OBJECT
            // =========================

            return OBJECT_MAPPER.readValue(
                json,
                BootstrapPayload.class
            );

        } catch (IOException e) {

            throw new IllegalStateException(
                "Failed to load bootstrap file: " + path, e
            );
        }
    }
}