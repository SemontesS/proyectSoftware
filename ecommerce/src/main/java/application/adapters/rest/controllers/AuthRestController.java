package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.LoginRequestDTO;
import application.adapters.rest.dtos.requests.RegisterBuyerRequestDTO;
import application.adapters.rest.dtos.responses.BuyerResponseDTO;
import application.adapters.rest.dtos.responses.LoginResponseDTO;
import application.adapters.rest.mappers.CommonRestMapper;
import application.adapters.rest.mappers.UserRestMapper;
import application.domain.exceptions.InvalidCredentialsException;
import application.domain.models.AuthenticationResult;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.ports.in.PublicAccessPort;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.IdentityDocument;
import application.domain.valueobjects.RawPassword;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    private final PublicAccessPort publicAccessPort;
    private final long expirationMs;

    public AuthRestController(PublicAccessPort publicAccessPort,
                              @Value("${jwt.expiration-ms:3600000}") long expirationMs) {
        this.publicAccessPort = publicAccessPort;
        this.expirationMs = expirationMs;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        // A malformed email or a password that breaks the policy can never be correct:
        // answer like any other wrong credential, without revealing the password policy.
        Email email;
        RawPassword password;
        try {
            email = new Email(request.email());
            password = new RawPassword(request.password());
        } catch (IllegalArgumentException e) {
            throw new InvalidCredentialsException("Invalid credentials.");
        }
        AuthenticationResult result = publicAccessPort.login(email, password);
        return ResponseEntity.ok(UserRestMapper.toLoginDTO(result, expirationMs / 1000));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal(expression = "user") User user) {
        publicAccessPort.logout(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register/buyer")
    public ResponseEntity<BuyerResponseDTO> registerBuyer(@Valid @RequestBody RegisterBuyerRequestDTO request) {
        Buyer buyer = publicAccessPort.registerBuyer(request.name(), new Email(request.email()),
                new IdentityDocument(request.identityDocument()),
                CommonRestMapper.toDomain(request.primaryAddress()), new RawPassword(request.password()));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserRestMapper.toBuyerDTO(buyer));
    }
}
