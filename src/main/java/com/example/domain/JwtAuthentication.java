package com.example.domain;

//import io.micronaut.security.annotation.Secured;
//import io.micronaut.security.rules.SecurityRule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Secured(SecurityRule.IS_AUTHENTICATED)
public class JwtAuthentication {

    private boolean authenticated;

    public boolean isAuthenticated() { return authenticated; }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

}
