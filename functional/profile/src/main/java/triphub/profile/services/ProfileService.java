package triphub.profile.services;

import triphub.profile.mappers.ProfileMapper;
import triphub.profile.models.Profile;
import triphub.profile.models.constants.ProfileRoles;
import triphub.profile.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    private final ProfileMapper profileMapper;

    public Mono<Profile> createProfile(Profile profile) {
        if (profile.getTagName() == null) profile.setTagName(profile.getEmail());
        if (profile.getName() == null) profile.setName(profile.getEmail());
        profile.setPassword(passwordEncoder.encode(profile.getPassword()));
        profile.setRole(ProfileRoles.INACTIVE);
        return profileRepository.save(profile);
    }

    public Mono<Profile> getProfileById(UUID id) {
        return profileRepository.findById(id);
    }

    public Flux<Profile> getProfilesByName(String name) {
        return profileRepository.findByName(name);
    }

    public Flux<Profile> getProfilesByTagNameContaining(String tagName) {
        return profileRepository.findByTagNameContaining(tagName);
    }

    public Flux<Profile> getProfilesByIds(List<UUID> ids) {
        return profileRepository.findAllById(ids);
    }

    public Mono<Profile> updateProfile(UUID id, Profile profile) {
        return profileRepository.findById(id)
                .flatMap(existing -> {
                    profileMapper.updateProfile(profile, existing);
                    return profileRepository.save(existing);
                });
    }

    public Mono<Profile> approveProfile(UUID id) {
        return profileRepository.findById(id)
                .flatMap(existing -> {
                    existing.setEnabled(true);
                    return profileRepository.save(existing);
                });
    }

    public Mono<Void> deleteProfile(UUID id) {
        return profileRepository.deleteById(id);
    }
}