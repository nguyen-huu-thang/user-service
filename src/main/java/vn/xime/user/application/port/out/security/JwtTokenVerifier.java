package vn.xime.user.application.port.out.security;

import vn.xime.user.domain.authentication.model.KeyContext;
import vn.xime.user.domain.authentication.model.JwtClaims;

public interface JwtTokenVerifier {

    JwtClaims verify(
        String token,
        KeyContext verificationKey
    );
}