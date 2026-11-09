package application.adapters.security;

import application.domain.models.User;
import application.domain.ports.out.JwtServicePort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * Output adapter: signs and verifies JWTs with HMAC-SHA. The token carries only identity data
 * ({@code sub} = user ID, {@code jti}, {@code role}, {@code ver} = token version); the user is
 * always reloaded from the database, so role or status changes apply immediately.
 */
@Component
public class JwtServiceAdapter implements JwtServicePort {

    private static final int MIN_SECRET_BYTES = 32;

    private final SecretKey key;
    private final long expirationMs;

    public JwtServiceAdapter(@Value("${jwt.secret}") String secret,
                             @Value("${jwt.expiration-ms:3600000}") long expirationMs) {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException("jwt.secret must have at least " + MIN_SECRET_BYTES + " bytes.");
        }
        this.key = Keys.hmacShaKeyFor(secretBytes);
        this.expirationMs = expirationMs;
    }

    @Override
    public String generateToken(User user, int tokenVersion) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(user.getUserId()))
                .id(UUID.randomUUID().toString())
                .claim("role", user.getRole().name())
                .claim("ver", tokenVersion)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    @Override
    public Optional<TokenClaims> parseToken(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            Integer version = claims.get("ver", Integer.class);
            if (version == null || claims.getSubject() == null) return Optional.empty();
            return Optional.of(new TokenClaims(Long.valueOf(claims.getSubject()), version));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
