package com.example.assessment.utils;

import com.example.assessment.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.MacProvider;
import jakarta.security.auth.message.AuthException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
@Component
public class JwtUtils {
    private static final long EXPIRATIONTIME = 30 * 60 * 1000; // 30 min
    private static final Key key = MacProvider.generateKey(); // 給定一組密鑰，用來解密以及加密使用

    public static String generateJwtToken(User user) {//角色身分
        JwtBuilder builder = Jwts.builder()
//                .claim("GRAPHQL",)
                .setId(String.valueOf(user.getUserId()))
                .setSubject(user.getPhone())
                .signWith(SignatureAlgorithm.HS256, key) // 金鑰
                // 設置過期時間
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME));
        return builder.compact();// 最後使用compact() 進行生成
    }

    /**
     * 解析JWT
     *
     * @param jwt 要解析的jwt
     * @return
     * @throws Exception
     */
    public Claims parseJWT(String jwt) throws AuthException {
        try {
            return Jwts
                    .parser()
                    .setSigningKey(key) // 設定簽名的祕鑰
                    .parseClaimsJws(jwt)
                    .getBody(); // 設定需要解析的jwt
        } catch (SignatureException e) {
            throw new AuthException("JWT signature 無效");
        } catch (MalformedJwtException e) {
            throw new AuthException("Token無效");
        } catch (ExpiredJwtException e) {
            throw new AuthException("Token過期");
        } catch (UnsupportedJwtException e) {
            throw new AuthException("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            throw new AuthException("JWT token compact of handler are invalid");
        }
    }
}
