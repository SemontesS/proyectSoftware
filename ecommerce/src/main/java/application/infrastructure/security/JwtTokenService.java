package application.infrastructure.security;
import application.domain.models.User;
import application.domain.ports.out.AuthenticationTokenPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtTokenService implements AuthenticationTokenPort {
    private static final long TOKEN_TTL_MILLIS = 3_600_000L;

    private final SecretKey signingKey;

    public JwtTokenService(@Value("${app.jwt.secret:change-me-in-production-please-use-a-strong-secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String issueFor(User user) {
        Date now = new Date();
        Date expiry = new Date(System.currentTimeMillis() + TOKEN_TTL_MILLIS);

        return Jwts.builder()
                .subject(user.getUserId().toString())
                .claim("email", user.getEmail().value())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
