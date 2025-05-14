package triphub.profile.configs.securityConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Data;
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
    @Value("${jwt.access-token.expiration}")
    private Integer accessTokenExpirationInSeconds;
    @Value("${jwt.refresh-token.expiration}")
    private Integer refreshTokenExpirationInSeconds;
    @Value("${jwt.issuer}")
    private String issuer;

    private TokenDetails generateAccessToken(Profile profile) {
        Map<String, Object> claims = new HashMap<>() {{
            put("role", profile.getRole());
            put("email", profile.getEmail());
            put("type", "ACCESS");
        }};
        return generateToken(claims, profile.getId().toString(), accessTokenExpirationInSeconds);
    }

    private TokenDetails generateRefreshToken(Profile profile) {
        Map<String, Object> claims = new HashMap<>() {{
            put("role", profile.getRole());
            put("email", profile.getEmail());
            put("type", "REFRESH");
        }};
        return generateToken(claims, profile.getId().toString(), refreshTokenExpirationInSeconds);
    }

    private TokenDetails generateToken(Map<String, Object> claims, String subject, Integer expirationInSeconds) {
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

    public Mono<TokenPair> authenticate(String email, String password) {
        return profileRepository.findByEmail(email)
                .flatMap(profile -> {
                    if (!profile.isEnabled()) {
                        return Mono.error(new AuthException("Account disabled", "USER_ACCOUNT_DISABLED"));
                    }

                    if (!passwordEncoder.matches(password, profile.getPassword())) {
                        return Mono.error(new AuthException("Invalid password", "INVALID_PASSWORD"));
                    }

                    TokenDetails accessToken = generateAccessToken(profile);
                    TokenDetails refreshToken = generateRefreshToken(profile);
                    
                    return Mono.just(new TokenPair(accessToken, refreshToken, profile.getId().toString()));
                })
                .switchIfEmpty(Mono.error(new AuthException("Invalid email", "INVALID_EMAIL")));
    }

    public Mono<TokenPair> refreshTokens(String refreshToken) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();

            String tokenType = claims.get("type", String.class);
            if (!"REFRESH".equals(tokenType)) {
                return Mono.error(new AuthException("Invalid token type", "INVALID_TOKEN_TYPE"));
            }

            String userId = claims.getSubject();
            String role = claims.get("role", String.class);
            String email = claims.get("email", String.class);

            Map<String, Object> accessClaims = new HashMap<>() {{
                put("role", role);
                put("email", email);
                put("type", "ACCESS");
            }};

            Map<String, Object> refreshClaims = new HashMap<>() {{
                put("role", role);
                put("email", email);
                put("type", "REFRESH");
            }};

            TokenDetails newAccessToken = generateToken(accessClaims, userId, accessTokenExpirationInSeconds);
            TokenDetails newRefreshToken = generateToken(refreshClaims, userId, refreshTokenExpirationInSeconds);

            return Mono.just(new TokenPair(newAccessToken, newRefreshToken, userId));
        } catch (Exception e) {
            return Mono.error(new AuthException("Invalid token", "INVALID_TOKEN"));
        }
    }

    @Data
    @AllArgsConstructor
    public static class TokenPair {
        private final TokenDetails accessToken;
        private final TokenDetails refreshToken;
        private final String profileId;
    }
}
