package com.example.assessment.configuration.security;


import com.example.assessment.model.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class TokenGenerator {
//    @Autowired
//    JwtEncoder accessTokenEncoder;
    private String createAccessToken(Authentication authentication) {
        MyUserDetails user = (MyUserDetails) authentication.getPrincipal();
        Instant now = Instant.now();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("myApp")
                .issuedAt(now)
                .expiresAt(now.plus(5, ChronoUnit.MINUTES))
                .subject(user.getId())
                .build();
//TODO:
        String token = JwtEncoderParameters.from(claimsSet).toString();
        return token;
    }

//    private String createRefreshToken(Authentication authentication) {
//        User user = (User) authentication.getPrincipal();
//        Instant now = Instant.now();
//
//        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
//                .issuer("myApp")
//                .issuedAt(now)
//                .expiresAt(now.plus(30, ChronoUnit.DAYS))
//                .subject(user.getId())
//                .build();
//
//        return refreshTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
//    }

    public Token createToken(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof MyUserDetails user)) {
            throw new BadCredentialsException(
                    MessageFormat.format("principal {0} is not of User type", authentication.getPrincipal().getClass())
            );
        }

        Token tokenDTO = new Token();
        tokenDTO.setUserId(Integer.valueOf(user.getId()));
        tokenDTO.setToken(createAccessToken(authentication));
        return tokenDTO;
    }
}
