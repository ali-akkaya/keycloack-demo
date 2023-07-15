package me.aliakkaya.keycloackdemo.config;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


//Main goal s here to add ROLE_ prefix with a converter since it is a default value while checking authorization
// See SecurityExpressionRoot constructor method.
@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {


    @Value("${keycloack.resourceId}")
    private  String resourceId;

    @Value("${keycloack.principleClaimName}")
    private  String principleClaimName;

    @Autowired
    private JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(source).stream(),
                extreactResourceRoles(source).stream()).collect(Collectors.toSet());
        return new JwtAuthenticationToken(
                source, authorities, principleClaimName
        );
    }

    private Collection<? extends GrantedAuthority>extreactResourceRoles(Jwt source){
        Map<String, Object> resourceAccess;
        Map<String, Object> resource;
        Collection<String> resourceRoles;

        if (source.getClaim("resource_access") == null) {
            return Set.of();
        }
        resourceAccess = source.getClaim("resource_access");

        if (resourceAccess.get(resourceId) == null) {
            return Set.of();
        }

        resource = (Map<String, Object>) resourceAccess.get(resourceId);

        resourceRoles = (Collection<String>) resource.get("roles");
        return resourceRoles
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}
