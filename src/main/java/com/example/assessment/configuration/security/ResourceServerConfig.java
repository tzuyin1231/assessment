package com.example.assessment.configuration.security;

import io.jsonwebtoken.impl.crypto.MacProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@EnableWebSecurity
@Configuration
public class ResourceServerConfig {
    private static final Logger log = LoggerFactory.getLogger(ResourceServerConfig.class);
    public static final SecretKey key = MacProvider.generateKey(); // 給定一組密鑰，用來解密以及加密使用

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("securityFilterChain啟用");
        http.cors().and()
                // TODO: 關閉CSRF(跨站請求偽造)攻擊的防護，這樣才不會拒絕外部直接對API 發出的請求，例如Postman 與前端
                .csrf().disable()
                // TODO: 禁用 HTTP 基本身份驗證，當客戶端試圖訪問受保護的資源時，Spring Security 將不再提示客戶端提供用戶名和密碼，而是直接返回未授權的響應或重定向到登錄頁面（如果您正在使用其他身份驗證方式）
                .httpBasic().disable()
                // TODO: 设置session是无状态的
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/token/**").permitAll()
                                .requestMatchers("/graphql").authenticated()
                )
                // 設定 OAuth2 的資源伺服器相關配置，使用 JWT 作為 token 的格式，並使用自訂的解碼器來解析 token
                .oauth2ResourceServer()
                .jwt().decoder(myCustomDecoder())
                // 設定 JWT 的授權轉換器，用於從 token 中提取權限信息
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
//                TODO:.exceptionHandling
                .and()
                // 設定 Bearer Token 的解析器，用於從request請求中解析出 Bearer Token
                .bearerTokenResolver(bearerTokenResolver())
                // 此时是认证失败
                // TODO: response Java class
                // TODO: oauth2 认证失败导致的，还有一种可能是非oauth2认证失败导致的，比如没有传递token，但是访问受权限保护的方法
                // authenticationEntryPoint:設定當請求需要授權但未通過驗證時的響應處理器
                .authenticationEntryPoint((request, response, exception) -> {
                    if (exception instanceof AuthenticationException) {
                        log.info("認證失敗，異常類型:[{}]", exception.getClass().getName());
                    }
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.setContentType(MediaType.APPLICATION_JSON.toString());
                    //TODO:回錯誤訊息給前端
                    response.getWriter().write("{\"code\":1,\"message\":\"您無權限訪問\"}");
                })
                // accessDeniedHandler:設定當請求的認證被成功解碼後沒有授權訪問時的響應處理器
                .accessDeniedHandler((request, response, exception) -> {
                    log.info("您無權限訪問，異常類型:[{}], {}", exception.getClass().getName(), request.getRequestURI());
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.setContentType(MediaType.APPLICATION_JSON.toString());
                    response.getWriter().write("{\"code\":2,\"message\":\"您無權限訪問\"}");
                })
        ;
        return http.build();
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

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(new JwtTimestampValidator(Duration.ofSeconds(10))));
        return decoder;
    }

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
    }
}
