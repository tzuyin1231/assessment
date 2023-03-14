package com.example.assessment.controller;

import com.example.assessment.configuration.security.ResourceServerConfig;
import com.example.assessment.repository.UserRepository;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@RestController
@CrossOrigin(origins = "*")
public class TokenController {
    private static final Logger log = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    UserRepository userRepository;

    @RequestMapping("/token/long")
    public String generateLongToken(){
        String longToken = generateToken(30*60);
        return longToken;
    }

    @RequestMapping("/token/short")
    public String generateShortToken(){
        String shortToken = generateToken(30);
        return shortToken;
    }

    public String generateToken(Integer time) {
        Instant now = Instant.now();
        /**
         * 生成JWT
         */
        log.info(String.valueOf(ResourceServerConfig.key));
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(Date.from(now)) // 簽發時間
                .signWith(SignatureAlgorithm.HS256, ResourceServerConfig.key) // 金鑰
                // 設置過期時間
                .setExpiration(Date.from(now.plus(time, ChronoUnit.SECONDS)));
        return builder.compact();// 最後使用compact() 進行生成
    }
}
