package application.domain.ports.out;

import application.domain.valueobjects.RawPassword;

public interface PasswordServicePort {
    String encrypt(RawPassword rawPassword);

    boolean matches(RawPassword rawPassword, String passwordHash);
}
