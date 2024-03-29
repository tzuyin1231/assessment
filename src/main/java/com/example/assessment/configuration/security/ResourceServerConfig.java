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
    public static final SecretKey key = MacProvider.generateKey(); // 給定一組密鑰，用來解密以及加密使用


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
        System.out.println("authentication設定");
        UserDetails admin = User.withUsername("admin").password(passwordEncoder.encode("adminPass")).roles("admin").build();
        UserDetails user = User.withUsername("user").password(passwordEncoder.encode("userPass")).roles("user").build();
        System.out.println(passwordEncoder.encode("userPass"));
        return new InMemoryUserDetailsManager(admin,user);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("securityFilterChain啟用");
        http
                // 關閉CSRF(跨站請求偽造)攻擊的防護，這樣才不會拒絕外部直接對API 發出的請求，例如Postman 與前端
                .csrf().disable().cors().disable().httpBasic().disable()
                .authorizeRequests()
                // 对于 userInfo 这个api 需要 s
                .requestMatchers("/token").permitAll()
                .requestMatchers("/graphql").authenticated()
                .and()
                // 设置session是无状态的
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .oauth2ResourceServer().jwt().decoder(myCustomDecoder())
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
//                .and()
//                .formLogin().and().build();
//                TODO:.exceptionHandling
                .and()
                // 从request请求那个地方中获取 token
                .bearerTokenResolver(bearerTokenResolver())
                // 此时是认证失败
                .authenticationEntryPoint((request, response, exception) -> {
                    // oauth2 认证失败导致的，还有一种可能是非oauth2认证失败导致的，比如没有传递token，但是访问受权限保护的方法
                    if (exception instanceof OAuth2AuthenticationException) {
                        OAuth2AuthenticationException oAuth2AuthenticationException = (OAuth2AuthenticationException) exception;
                        OAuth2Error error = oAuth2AuthenticationException.getError();
                        log.info("认证失败,异常类型:[{}],异常:[{}]", exception.getClass().getName(), error);
                    }
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.setContentType(MediaType.APPLICATION_JSON.toString());
                    response.getWriter().write("{\"code\":-3,\"message\":\"您无权限访问\"}");
                })
                // 认证成功后，无权限访问
                .accessDeniedHandler((request, response, exception) -> {
                    log.info("您无权限访问,异常类型:[{}]", exception.getClass().getName());
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.setContentType(MediaType.APPLICATION_JSON.toString());
                    response.getWriter().write("{\"code\":-4,\"message\":\"您无权限访问\"}");
                })

        ;
        DefaultSecurityFilterChain build = http.build();
        return build;
    }

    /**
     * 从request请求中那个地方获取到token
     */
    private BearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();
        // 设置请求头的参数，即从这个请求头中获取到token
        bearerTokenResolver.setBearerTokenHeaderName(HttpHeaders.AUTHORIZATION);
        bearerTokenResolver.setAllowFormEncodedBodyParameter(false);
        // 是否可以从uri请求参数中获取token
        bearerTokenResolver.setAllowUriQueryParameter(false);
        return bearerTokenResolver;
    }

    private JwtDecoder myCustomDecoder() {
        System.out.println("jwtDecoder: "+key);
        return NimbusJwtDecoder.withSecretKey(key).build();
//        簡中教程
//        public JwtDecoder jwtDecoder(RestTemplateBuilder builder) {
            // 授权服务器 jwk 的信息
//            NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri("http://qq.com:8080/oauth2/jwks")
//                    // 设置获取 jwk 信息的超时时间
//                    .restOperations(
//                            builder.setReadTimeout(Duration.ofSeconds(3))
//                                    .setConnectTimeout(Duration.ofSeconds(3))
//                                    .build()
//                    )
//                    .build();
//            // 对jwt进行校验
//            decoder.setJwtValidator(JwtValidators.createDefault());
//            // 对 jwt 的 claim 中增加值
//            decoder.setClaimSetConverter(
//                    MappedJwtClaimSetConverter.withDefaults(Collections.singletonMap("为claim中增加key", custom -> "值"))
//            );
//            return decoder;
//        }
//
//        try{
//            System.out.println(token);
//            JwsHeader headers = Jwts.parser()
//                    .setSigningKey(key) // 設定簽名的祕鑰
//                    .parseClaimsJws(token).getHeader();
//            Map<String, Object> claims = Jwts.parser()
//                    .setSigningKey(key) // 設定簽名的祕鑰
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
     * 从 JWT 的 scope 中获取的权限 取消 SCOPE_ 的前缀
     * 设置从 jwt claim 中那个字段获取权限
     * 如果需要同多个字段中获取权限或者是通过url请求获取的权限，则需要自己提供jwtAuthenticationConverter()这个方法的实现
     *
     * @return JwtAuthenticationConverter
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//        ethan傳的簡中教程
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 去掉 SCOPE_ 的前缀
        authoritiesConverter.setAuthorityPrefix("");
        // 从jwt claim 中那个字段获取权限，模式是从 scope 或 scp 字段中获取
        authoritiesConverter.setAuthoritiesClaimName("scope");
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;

//        YT教程
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
