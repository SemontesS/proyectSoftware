package application.domain.ports.out;

import application.domain.models.User;

public interface AuthenticationTokenPort {
    String issueFor(User user);
}
