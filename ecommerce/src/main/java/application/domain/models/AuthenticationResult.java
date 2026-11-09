package application.domain.models;

/** Outcome of a successful login: the signed token and the authenticated user. */
public record AuthenticationResult(String token, User user) {
}
