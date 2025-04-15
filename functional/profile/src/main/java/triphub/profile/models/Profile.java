package triphub.profile.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import triphub.profile.models.constants.ProfileRoles;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("profiles")
public class Profile {
    @Id
    private UUID id;
    private String email;
    private String password;
    private String name;
    private String country;
    private LocalDate birthDate;
    private String tagName;
    private ProfileRoles role;
    private boolean enabled;
}