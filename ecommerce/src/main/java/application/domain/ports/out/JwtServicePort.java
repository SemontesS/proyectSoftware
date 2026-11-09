package application.domain.ports.out;

import application.domain.models.User;

import java.util.Optional;

public interface JwtServicePort {

    /** Identity data extracted from a valid token. */
    record TokenClaims(Long userId, int tokenVersion) {
    }

    /** Issues a signed token carrying the user ID ({@code sub}), the role and the token version. */
    String generateToken(User user, int tokenVersion);

    /** Returns the claims if the token has a valid signature and has not expired; empty otherwise. */
    Optional<TokenClaims> parseToken(String token);
}
