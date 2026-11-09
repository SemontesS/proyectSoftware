package application.domain.valueobjects;

/**
 * Plain-text password as typed by the user. It only exists while a request is processed:
 * it is validated here, hashed by the PasswordServicePort and never stored nor printed.
 */
public record RawPassword(String value) {
    public RawPassword {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Password is required.");
        boolean hasLetter = value.chars().anyMatch(Character::isLetter);
        boolean hasDigit = value.chars().anyMatch(Character::isDigit);
        if (value.length() < 8 || !hasLetter || !hasDigit) {
            throw new IllegalArgumentException("Password must have at least 8 characters, a letter and a digit.");
        }
    }

    @Override
    public String toString() {
        return "RawPassword[****]";
    }
}
