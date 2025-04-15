package triphub.profile.repositories;

import triphub.profile.models.Profile;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProfileRepository extends ReactiveCrudRepository<Profile, UUID> {
    @Query("SELECT * FROM profiles WHERE name ILIKE '%' || :name || '%'")
    Flux<Profile> findByName(String name);
    Mono<Profile> findByEmail(String email);
    Mono<Profile> findByTagName(String tagName);
    
    Flux<Profile> findByTagNameContaining(String tagName);
}