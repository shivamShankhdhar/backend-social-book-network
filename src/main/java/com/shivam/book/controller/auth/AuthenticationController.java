package com.shivam.book.controller.auth;

import com.shivam.book.requests.auth.AuthenticationRequest;
import com.shivam.book.requests.auth.RegistrationRequest;
import com.shivam.book.responses.auth.AuthenticationResponse;
import com.shivam.book.service.user.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name="authentication")
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(

            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException {
        System.out.println("hitting");
        service.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate-user")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ){
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/activate-account")
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        service.activateAccount(token);
    }
}
