package triphub.profile.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import triphub.profile.DTOs.AuthRequest;
import triphub.profile.DTOs.ProfileDTO;
import triphub.profile.configs.securityConfig.SecurityService;
import triphub.profile.mappers.ProfileMapper;
import triphub.profile.models.Profile;
import triphub.profile.services.ProfileService;
import triphub.profile.services.RabbitService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    @Value("${jwt.access-token.expiration}")
    private Integer accessTokenExpirationInSeconds;
    
    @Value("${jwt.refresh-token.expiration}")
    private Integer refreshTokenExpirationInSeconds;
 
    private final SecurityService securityService;
    private final ProfileService profileService;
    private final ProfileMapper profileMapper;
    private final RabbitService rabbitService;

    @PostMapping("/register")
    public Mono<ResponseEntity<Void>> register(@RequestBody ProfileDTO dto) {
        Profile profile = profileMapper.map(dto);
        return profileService.createProfile(profile)
                .flatMap(savedProfile -> rabbitService.sendRegistrationNotification(savedProfile)
                .thenReturn(ResponseEntity.noContent().<Void>build()));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Void>> login(@RequestBody AuthRequest dto, ServerWebExchange exchange) {
        return securityService.authenticate(dto.getEmail(), dto.getPassword())
            .flatMap(tokenPair -> {
                // Set access token cookie
                ResponseCookie accessCookie = ResponseCookie.from("access_token", tokenPair.getAccessToken().getToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(accessTokenExpirationInSeconds)
                    .sameSite("Lax")
                    .build();

                // Set refresh token cookie
                ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tokenPair.getRefreshToken().getToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(refreshTokenExpirationInSeconds)
                    .sameSite("Lax")
                    .build();
                
                ResponseCookie profileCookie = ResponseCookie.from(
                    "profile_id",
                    tokenPair.getProfileId()
                )
                    .path("/")
                    .maxAge(refreshTokenExpirationInSeconds)
                    .build();

                exchange.getResponse().addCookie(profileCookie);
                exchange.getResponse().addCookie(accessCookie);
                exchange.getResponse().addCookie(refreshCookie);
                return Mono.just(ResponseEntity.noContent().<Void>build());
            });
    }

    @GetMapping("/approve/{profileId}")
    public Mono<ResponseEntity<Void>> approve(@PathVariable UUID profileId) {
        return profileService.approveProfile(profileId)
            .map(Void -> ResponseEntity.noContent().<Void>build());
    }

    @GetMapping("/refresh")
    public Mono<ResponseEntity<Void>> refresh(
        @CookieValue(name = "refresh_token", required = false) String refreshToken,
        ServerWebExchange exchange
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            exchange.getResponse().addCookie(ResponseCookie.from("access_token", "").path("/").maxAge(0).build());
            exchange.getResponse().addCookie(ResponseCookie.from("refresh_token", "").path("/").build());
            exchange.getResponse().addCookie(ResponseCookie.from("profile_id", "").path("/").build());
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        return securityService.refreshTokens(refreshToken)
            .flatMap(tokenPair -> {
                // Set new access token cookie
                ResponseCookie accessCookie = ResponseCookie.from("access_token", tokenPair.getAccessToken().getToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(accessTokenExpirationInSeconds)
                    .sameSite("Lax")
                    .build();

                // Set new refresh token cookie
                ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tokenPair.getRefreshToken().getToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(refreshTokenExpirationInSeconds)
                    .sameSite("Lax")
                    .build();

                ResponseCookie profileCookie = ResponseCookie.from(
                    "profile_id",
                    tokenPair.getProfileId()
                )
                    .path("/")
                    .maxAge(refreshTokenExpirationInSeconds)
                    .build();

                exchange.getResponse().addCookie(accessCookie);
                exchange.getResponse().addCookie(refreshCookie);
                exchange.getResponse().addCookie(profileCookie);
                return Mono.just(ResponseEntity.ok().build());
            });
    }

    @GetMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(ServerWebExchange exchange) {
        exchange.getResponse().addCookie(ResponseCookie.from("access_token", "").path("/").maxAge(0).build());
        exchange.getResponse().addCookie(ResponseCookie.from("refresh_token", "").path("/").build());
        exchange.getResponse().addCookie(ResponseCookie.from("profile_id", "").path("/").build());
        return Mono.just(ResponseEntity.noContent().<Void>build());
    }
}
