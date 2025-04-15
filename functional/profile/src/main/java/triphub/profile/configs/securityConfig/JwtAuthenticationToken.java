package triphub.profile.configs.securityConfig;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final CustomPrincipal principal;
    private final String credentials;

    public JwtAuthenticationToken(
        CustomPrincipal principal,
        String credentials,
        Collection<? extends GrantedAuthority> authorities
    ) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public CustomPrincipal getPrincipal() {
        return this.principal;
    }
}
