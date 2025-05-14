package triphub.profile.controllers;

import org.springframework.http.ResponseEntity;
import triphub.profile.models.Profile;
import triphub.profile.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Profile>> getProfileById(@PathVariable UUID id) {
        return profileService.getProfileById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/by_ids")
    public Flux<Profile> getProfilesByIds(@RequestBody List<UUID> ids) {
        return profileService.getProfilesByIds(ids);
    }

    @GetMapping("/by_tagname/{tagName}")
    public Flux<Profile> getProfilesByTagName(@PathVariable String tagName) {
        return profileService.getProfilesByTagNameContaining(tagName);
    }

    @GetMapping("/by_name/{name}")
    public Flux<Profile> getProfilesByName(@PathVariable String name) {
        return profileService.getProfilesByName(name);
    }

    @GetMapping("/by_email/{email}")
    public Mono<Profile> getProfilesByEmail(@PathVariable String email) {
        return profileService.getProfilesByEmail(email);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateProfile(@PathVariable UUID id, @RequestBody Profile profile) {
        return profileService.updateProfile(id, profile)
                .map(Void -> ResponseEntity.noContent().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProfile(@PathVariable UUID id) {
        return profileService.deleteProfile(id)
            .thenReturn(ResponseEntity.noContent().build());
    }
}