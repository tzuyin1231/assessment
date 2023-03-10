package com.example.assessment.configuration.security;

import com.example.assessment.model.UserService;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@EnableWebSecurity
@Configuration
public class ResourceServerConfig {
    private static final Logger log = LoggerFactory.getLogger(ResourceServerConfig.class);
    public static final long EXPIRATIONTIME = 30 * 60 * 1000; // 30 min
    public static final SecretKey key = MacProvider.generateKey(); // ???????????????????????????????????????????????????


    @Autowired
    JwtToUserConverter jwtToUserConverter;

//    @Autowired
//    PasswordEncoder passwordEncoder;
//    @Autowired
//    private RestTemplateBuilder restTemplateBuilder;
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
//    authentication
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        System.out.println("authentication??????");
        UserDetails admin = User.withUsername("admin").password(passwordEncoder.encode("adminPass")).roles("admin").build();
        UserDetails user = User.withUsername("user").password(passwordEncoder.encode("userPass")).roles("user").build();
        System.out.println(passwordEncoder.encode("userPass"));
        return new InMemoryUserDetailsManager(admin,user);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("securityFilterChain??????");
        http
                // ??????CSRF(??????????????????)??????????????????????????????????????????????????????API ????????????????????????Postman ?????????
                .csrf().disable().cors().disable().httpBasic().disable()
                .authorizeRequests()
                // ?????? userInfo ??????api ?????? s
                .requestMatchers("/token").permitAll()
                .requestMatchers("/graphql").authenticated()
                .and()
                // ??????session???????????????
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .oauth2ResourceServer().jwt().decoder(myCustomDecoder())
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
//                .and()
//                .formLogin().and().build();
//                TODO:.exceptionHandling
                .and()
                // ???request??????????????????????????? token
                .bearerTokenResolver(bearerTokenResolver())
                // ?????????????????????
                .authenticationEntryPoint((request, response, exception) -> {
                    // oauth2 ????????????????????????????????????????????????oauth2??????????????????????????????????????????token???????????????????????????????????????
                    if (exception instanceof OAuth2AuthenticationException) {
                        OAuth2AuthenticationException oAuth2AuthenticationException = (OAuth2AuthenticationException) exception;
                        OAuth2Error error = oAuth2AuthenticationException.getError();
                        log.info("????????????,????????????:[{}],??????:[{}]", exception.getClass().getName(), error);
                    }
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.setContentType(MediaType.APPLICATION_JSON.toString());
                    response.getWriter().write("{\"code\":-3,\"message\":\"??????????????????\"}");
                })
                // ?????????????????????????????????
                .accessDeniedHandler((request, response, exception) -> {
                    log.info("??????????????????,????????????:[{}]", exception.getClass().getName());
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.setContentType(MediaType.APPLICATION_JSON.toString());
                    response.getWriter().write("{\"code\":-4,\"message\":\"??????????????????\"}");
                })

        ;
        DefaultSecurityFilterChain build = http.build();
        return build;
    }

    /**
     * ???request??????????????????????????????token
     */
    private BearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();
        // ????????????????????????????????????????????????????????????token
        bearerTokenResolver.setBearerTokenHeaderName(HttpHeaders.AUTHORIZATION);
        bearerTokenResolver.setAllowFormEncodedBodyParameter(false);
        // ???????????????uri?????????????????????token
        bearerTokenResolver.setAllowUriQueryParameter(false);
        return bearerTokenResolver;
    }

    private JwtDecoder myCustomDecoder() {
        System.out.println("jwtDecoder: "+key);
        return NimbusJwtDecoder.withSecretKey(key).build();
//        ????????????
//        public JwtDecoder jwtDecoder(RestTemplateBuilder builder) {
            // ??????????????? jwk ?????????
//            NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri("http://qq.com:8080/oauth2/jwks")
//                    // ???????????? jwk ?????????????????????
//                    .restOperations(
//                            builder.setReadTimeout(Duration.ofSeconds(3))
//                                    .setConnectTimeout(Duration.ofSeconds(3))
//                                    .build()
//                    )
//                    .build();
//            // ???jwt????????????
//            decoder.setJwtValidator(JwtValidators.createDefault());
//            // ??? jwt ??? claim ????????????
//            decoder.setClaimSetConverter(
//                    MappedJwtClaimSetConverter.withDefaults(Collections.singletonMap("???claim?????????key", custom -> "???"))
//            );
//            return decoder;
//        }
//
//        try{
//            System.out.println(token);
//            JwsHeader headers = Jwts.parser()
//                    .setSigningKey(key) // ?????????????????????
//                    .parseClaimsJws(token).getHeader();
//            Map<String, Object> claims = Jwts.parser()
//                    .setSigningKey(key) // ?????????????????????
//                    .parseClaimsJws(token).getBody();
//            Jwt jwt = new Jwt(
//                    token,
//                    Instant.now(),
//                    Instant.now().plus(10, ChronoUnit.MINUTES),
//                    claims,
//                    headers);
//            return jwt;
//        }catch (Exception e){
//            log.info(String.valueOf(e));
//            System.out.println(token);
//            return null;
//        }}
    }

//    @Bean
//    JwtEncoder myCustomEncoder() {
//        JWK jwk = new RSAKey
//                .Builder(keyUtils.getAccessTokenPublicKey())
//                .privateKey(keyUtils.getAccessTokenPrivateKey())
//                .build();
//        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
//        NimbusJwtEncoder()
//        return new NimbusJwtEncoder(jwks);
//    }

    /**
     * ??? JWT ??? scope ?????????????????? ?????? SCOPE_ ?????????
     * ????????? jwt claim ???????????????????????????
     * ?????????????????????????????????????????????????????????url?????????????????????????????????????????????jwtAuthenticationConverter()?????????????????????
     *
     * @return JwtAuthenticationConverter
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//        ethan??????????????????
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // ?????? SCOPE_ ?????????
        authoritiesConverter.setAuthorityPrefix("");
        // ???jwt claim ?????????????????????????????????????????? scope ??? scp ???????????????
        authoritiesConverter.setAuthoritiesClaimName("scope");
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;

//        YT??????
//        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtRefreshTokenDecoder());
//        provider.setJwtAuthenticationConverter(jwtToUserConverter);
//        return provider;
    }



//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setPasswordEncoder(passwordEncoder());
//
//        return authProvider;
//    }

}
