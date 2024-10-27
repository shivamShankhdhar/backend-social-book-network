package com.shivam.book.service.user;

import com.shivam.book.Utilities.EmailTemplateName;
import com.shivam.book.requests.auth.AuthenticationRequest;
import com.shivam.book.responses.auth.AuthenticationResponse;
import com.shivam.book.requests.auth.RegistrationRequest;
import com.shivam.book.model.user.Token;
import com.shivam.book.model.user.User;
import com.shivam.book.repository.user.RoleRepository;
import com.shivam.book.repository.user.TokenRepository;
import com.shivam.book.repository.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    private final String activationUrl = "http://localhost:3000/activate-account";

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
                // todo better exception handling
                .orElseThrow(()-> new IllegalArgumentException("ROLE USER was not initialized"));

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder().encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendVerificationEmail(user);
    }

    private void sendVerificationEmail(User user) throws MessagingException {
        System.out.println("Sending mail...");
        var newToken = generateAndSaveActivationToken(user);
//        send email

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account Activation"
        );

    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for(int i=0; i<length; i++){
            int randomIndex = secureRandom.nextInt(characters.length()); // 0 -> 9
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(@Valid AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
                  request.getEmail(),
                  request.getPassword()
          )
        );

        var claims = new HashMap<String,Object>();
        var user = ((User)auth.getPrincipal());

        claims.put("fullName",user.getFullName());
        var jwtToken = jwtService.generateToken(claims,user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public void activateAccount(String token) throws MessagingException {
        Token savedTokenFromDB = tokenRepository.findByToken(token)
                .orElseThrow(()->new RuntimeException("Invalid token"));

        if(LocalDateTime.now().isAfter(savedTokenFromDB.getExpiresAt())){
            sendVerificationEmail(savedTokenFromDB.getUser());

            throw new RuntimeException("Activation token has expired.A new token has been sent.");
        }

        var user = userRepository.findById(savedTokenFromDB.getUser().getId())
                .orElseThrow(()->new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        savedTokenFromDB.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedTokenFromDB);
    }
}
