package me.aliakkaya.keycloackdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

@Component
public class GrantedConverter {

    @Bean
    public JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter(){
        return new JwtGrantedAuthoritiesConverter();
    }
}
