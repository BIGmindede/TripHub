package triphub.profile.configs.securityConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import triphub.profile.exceptionHandling.exceptions.UnauthorizedException;
import triphub.profile.models.Profile;
import triphub.profile.services.ProfileService;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final ProfileService profileService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        
        return profileService.getProfileById(principal.getId())
                .filter(Profile::isEnabled)
                .switchIfEmpty(Mono.error(new UnauthorizedException("User disabled")))
                .map(profile -> authentication);
    }
}
