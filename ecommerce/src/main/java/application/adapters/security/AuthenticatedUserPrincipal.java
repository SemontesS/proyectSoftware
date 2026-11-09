package application.adapters.security;

import application.domain.models.User;
import application.domain.valueobjects.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Spring Security view of the authenticated domain user. Controllers receive the domain
 * {@link User} through {@code @AuthenticationPrincipal(expression = "user")}, which reads
 * {@link #getUser()}.
 */
public class AuthenticatedUserPrincipal implements UserDetails {

    private final User user;

    public AuthenticatedUserPrincipal(User user) {
        this.user = Objects.requireNonNull(user, "User is required.");
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    /** Authentication is token based: the password hash is never loaded into the security context. */
    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.getEmail().value();
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVE;
    }
}
