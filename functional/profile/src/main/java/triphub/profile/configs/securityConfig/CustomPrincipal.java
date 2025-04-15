package triphub.profile.configs.securityConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomPrincipal implements Principal {
    private UUID id;
    private String name;
}
