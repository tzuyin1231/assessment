package com.example.assessment.utils;

import com.example.assessment.model.User;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    private static final long EXPIRATIONTIME = 30 * 60 * 1000; // 30 min
    private static final Key key = MacProvider.generateKey(); // 給定一組密鑰，用來解密以及加密使用
    public static String generateJwtToken(User user) {//角色身分
        JwtBuilder builder = Jwts.builder()
                .setId(String.valueOf(user.getUserId()))
                .setSubject(user.getPhone())
                .signWith(SignatureAlgorithm.HS256, key) // 金鑰
                // 設置過期時間
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME));
        return builder.compact();// 最後使用compact() 進行生成
    }
}
