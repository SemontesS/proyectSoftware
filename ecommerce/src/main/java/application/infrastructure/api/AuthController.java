package application.infrastructure.api;

import application.domain.models.AuthenticationResult;
import application.domain.services.authorization.AuthenticateUserService;
import application.domain.valueobjects.Credentials;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticateUserService authenticateUserService;

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(@RequestBody AuthLoginRequest request) {
        AuthenticationResult result = authenticateUserService.authenticate(new Credentials(request.email(), request.password()));
        return ResponseEntity.ok(new AuthLoginResponse(result.token()));
    }

    public record AuthLoginRequest(String email, String password) {
    }

    public record AuthLoginResponse(String token) {
    }
}
