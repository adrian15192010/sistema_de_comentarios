package com.microservice.authservice.service;

import com.microservice.authservice.dto.AuthRequest;
import com.microservice.authservice.dto.AuthenticationTokenResponse;
import com.microservice.authservice.dto.RegisterRequest;
import com.microservice.authservice.dto.TokenResponse;
import com.microservice.authservice.entities.TokenInvalid;
import com.microservice.authservice.entities.TokenInvalidRepository;
import com.microservice.authservice.entities.User;
import com.microservice.authservice.persistence.Token;
import com.microservice.authservice.persistence.TokenRepository;
import com.microservice.authservice.persistence.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenInvalidRepository tokenInvalidRepository;


    public TokenResponse register(final RegisterRequest request) {
        final User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        final User savedUser = repository.save(user);
        final String jwtToken = jwtService.generateToken(savedUser);
        final String refreshToken = jwtService.generateRefreshToken(savedUser);

        saveUserToken(savedUser, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);
    }

    public TokenResponse authenticate(final AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        final User user = repository.findByEmail(request.email())
                .orElseThrow();
        final String accessToken = jwtService.generateToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        return new TokenResponse(accessToken, refreshToken);
    }

    private void saveUserToken(User user, String jwtToken) {
        final Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(final User user) {
        final List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (!validUserTokens.isEmpty()) {
            validUserTokens.forEach(token -> {
                token.setIsExpired(true);
                token.setIsRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }
    }

    public TokenResponse refreshToken(@NotNull final String authentication) {

        if (authentication == null || !authentication.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid auth header");
        }
        final String refreshToken = authentication.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail == null) {
            return null;
        }

        final User user = this.repository.findByEmail(userEmail).orElseThrow();
        final boolean isTokenValid = jwtService.isTokenValid(refreshToken, user);
        if (!isTokenValid) {
            return null;
        }

        final String accessToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);

        return new TokenResponse(accessToken, refreshToken);
    }

    public String GetUsername (){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }

    public AuthenticationTokenResponse authenticationTokenResponse (String token){
        try {

            Optional<TokenInvalid> tokenInvalid = tokenInvalidRepository.findByToken(token);

            String email = jwtService.extractUsername(token);

            User user = userRepository.findByEmail(email).orElseThrow();

            if(jwtService.isTokenValid(token, user) && !tokenInvalid.isPresent()){
                return AuthenticationTokenResponse.builder()
                        .userId(user.getId())
                        .email(email)
                        .isValid(true)
                        .build();
            }


        }catch (Exception e){

            return AuthenticationTokenResponse.builder()
                    .userId(null)
                    .email(null)
                    .isValid(false)
                    .build();

        }

            return AuthenticationTokenResponse.builder()
                    .userId(null)
                    .email(null)
                    .isValid(false)
                    .build();
    }

    public String log_out(String authentication){

        String token = authentication.substring(7);

        TokenInvalid tokenInvalid = TokenInvalid.builder()
                .token(token)
                .build();

        tokenInvalidRepository.save(tokenInvalid);

        return "Sesion Cerrada";
    }

}
