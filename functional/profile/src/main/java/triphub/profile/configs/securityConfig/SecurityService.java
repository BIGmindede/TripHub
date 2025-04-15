package triphub.profile.configs.securityConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import triphub.profile.exceptionHandling.exceptions.AuthException;
import triphub.profile.models.Profile;
import triphub.profile.repositories.ProfileRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Integer expirationInSeconds;
    @Value("${jwt.issuer}")
    private String issuer;

    private TokenDetails generateToken(Profile profile) {
        Map<String, Object> claims = new HashMap<>() {{
            put("role", profile.getRole());
            put("email", profile.getEmail());
        }};
        return generateToken(claims, profile.getId().toString());
    }

    private TokenDetails generateToken(Map<String, Object> claims, String subject) {
        Long expirationTimeInMillis = expirationInSeconds * 1000L;
        Date expirationDate = new Date(new Date().getTime() + expirationTimeInMillis);

        return generateToken(expirationDate, claims, subject);
    }

    private TokenDetails generateToken(Date expirationDate, Map<String, Object> claims, String subject) {
        Date createdDate = new Date();
        String token = Jwts.builder()
                .claims(claims)
                .issuer(issuer)
                .subject(subject)
                .issuedAt(createdDate)
                .id(UUID.randomUUID().toString())
                .expiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), Jwts.SIG.HS256)
                .compact();

        return TokenDetails.builder()
                .token(token)
                .issuedAt(createdDate)
                .expiresAt(expirationDate)
                .build();
    }

    public Mono<TokenDetails> authenticate(String email, String password) {
        return profileRepository.findByEmail(email)
                .flatMap(profile -> {
                    if (!profile.isEnabled()) {
                        return Mono.error(new AuthException("Account disabled", "USER_ACCOUNT_DISABLED"));
                    }

                    if (!passwordEncoder.matches(password, profile.getPassword())) {
                        return Mono.error(new AuthException("Invalid password", "INVALID_PASSWORD"));
                    }

                    return Mono.just(generateToken(profile));
                })
                .switchIfEmpty(Mono.error(new AuthException("Invalid email", "INVALID_EMAIL")));
    }

    public Mono<TokenDetails> refreshToken(String currentToken) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(currentToken)
                .getPayload();

            Date expirationInDate = new Date(System.currentTimeMillis() + expirationInSeconds * 1000L);

            String newToken = Jwts.builder()
                .subject(claims.getSubject())
                .issuedAt(new Date())
                .expiration(expirationInDate)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), Jwts.SIG.HS256)
                .compact();

            return Mono.just(
                TokenDetails.builder()
                    .token(newToken)
                    .expiresAt(expirationInDate)
                    .build()
            );
        } catch (Exception e) {
            return Mono.error(new AuthException("Invalid token", "INVALID_TOKEN"));
        }
    }
}
