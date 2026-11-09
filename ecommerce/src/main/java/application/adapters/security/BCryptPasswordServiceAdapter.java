package application.adapters.security;

import application.domain.ports.out.PasswordServicePort;
import application.domain.valueobjects.RawPassword;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/** Output adapter: BCrypt implementation of the password port. */
@Component
public class BCryptPasswordServiceAdapter implements PasswordServicePort {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String encrypt(RawPassword rawPassword) {
        return encoder.encode(rawPassword.value());
    }

    @Override
    public boolean matches(RawPassword rawPassword, String passwordHash) {
        return encoder.matches(rawPassword.value(), passwordHash);
    }
}
