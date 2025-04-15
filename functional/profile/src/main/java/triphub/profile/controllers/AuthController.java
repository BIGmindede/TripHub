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

    @Value("${jwt.expiration}")
    private Integer expirationInSeconds;
 
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
            .flatMap(newToken -> {
                ResponseCookie cookie = ResponseCookie.from("jwt", newToken.getToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(System.currentTimeMillis() + expirationInSeconds * 1000L)
                    .sameSite("Lax")
                    .build();

                exchange.getResponse().addCookie(cookie);
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
        @CookieValue(name = "jwt", required = false) String token,
        ServerWebExchange exchange
    ) {
        if (token == null || token.isBlank()) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        return securityService.refreshToken(token)
            .flatMap(newToken -> {
                ResponseCookie cookie = ResponseCookie.from("jwt", newToken.getToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(System.currentTimeMillis() + expirationInSeconds * 1000L)
                    .sameSite("Lax")
                    .build();

                exchange.getResponse().addCookie(cookie);
                return Mono.just(ResponseEntity.ok().build());
            });
    }
}
