package com.black.monkey.my.election.commons.api.security;


import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.source.DefaultJWKSetCache;
import com.nimbusds.jose.jwk.source.JWKSetCache;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Configures our application with Spring Security to restrict access to our API endpoints.
 */
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*
        This is where we configure the security required for our endpoints and setup our app to serve as
        an OAuth2 Resource Server, using JWT validation.
        */
        http
                .csrf().disable().cors().disable()
                .authorizeRequests()
                .mvcMatchers("/api/public").permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .mvcMatchers("/api/private-scoped").hasAuthority("SCOPE_read:messages") // will not work with Auth0
                .mvcMatchers("/api/v1/open-crv").authenticated()
                .mvcMatchers("/api/v1/close-crv").authenticated()
                .mvcMatchers("/api/v1/note").authenticated()
                .antMatchers(HttpMethod.POST,"/api/v1/vote-registration").authenticated()
                .antMatchers(HttpMethod.DELETE,"/api/v1/vote-registration").authenticated()
                .mvcMatchers("/api/v1/crv-lookup/my-crv").authenticated()
                .and().oauth2ResourceServer().jwt();

        http.cors().configurationSource(corsConfigurationSource());

        return http.build();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration cors = new CorsConfiguration().applyPermitDefaultValues();
        cors.setAllowedMethods(Arrays.asList("PUT", "GET", "POST", "OPTIONS"));
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

    @Value("${spring.security.oauth2.resourceserver.jwt.jwks}")
    private String jwks;

    @Bean
    public JwtDecoder jwtDecoder() throws KeySourceException, MalformedURLException {

        JWKSetCache jwkSetCache = new DefaultJWKSetCache(500, 400, TimeUnit.MINUTES);
        RemoteJWKSet<SecurityContext> jwkSet = new RemoteJWKSet<>(new URL(jwks), null, jwkSetCache);
        JWSKeySelector<SecurityContext> jwsKeySelector = JWSAlgorithmFamilyJWSKeySelector.fromJWKSource(jwkSet);

        DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSKeySelector(jwsKeySelector);

        return new NimbusJwtDecoder(jwtProcessor);
    }


//    @Value("${auth0.audience}")
//    private String audience;
//
//    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
//    private String issuer;
//    @Bean
//    JwtDecoder jwtDecoder() {
//        /*
//        By default, Spring Security does not validate the "aud" claim of the token, to ensure that this token is
//        indeed intended for our app. Adding our own validator is easy to do:
//        */
//
//        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder)
//                JwtDecoders.fromOidcIssuerLocation(issuer);
//
//        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
//        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
//        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
//
//        jwtDecoder.setJwtValidator(withAudience);
//
//        return jwtDecoder;
//    }

}
