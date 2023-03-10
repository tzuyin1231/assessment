package com.example.assessment.utils;

import com.example.assessment.configuration.security.MyUserDetails;
import com.example.assessment.model.Token;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.MacProvider;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
@Component
public class JwtUtils {
    @Autowired
    static JwtEncoder accessTokenEncoder;

    private static final long EXPIRATIONTIME = 30 * 60 * 1000; // 30 min
    private static final Key key = MacProvider.generateKey(); // 給定一組密鑰，用來解密以及加密使用

    public static String createAccessToken(Authentication authentication) {//角色身分
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Instant now = Instant.now();
//        JwtBuilder builder = Jwts.builder()
////                .claim("GRAPHQL",)
//                .setId(String.valueOf(user.getUserId()))
//                .setSubject(user.getPhone())
//                .signWith(SignatureAlgorithm.HS256, key) // 金鑰
//                // 設置過期時間
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME));
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("myApp")
                .issuedAt(now)
                .expiresAt(now.plus(10, ChronoUnit.MINUTES))
//                .subject(String.valueOf(myUserDetails.getId())).build();
                .subject(String.valueOf(1)).build();

        return accessTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();// 最後使用compact() 進行生成
    }

    public Token createToken(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof MyUserDetails myUserDetails)) {
            throw new BadCredentialsException(
                    MessageFormat.format("principal {0} is not of User type", authentication.getPrincipal().getClass())
            );
        }

        Token token = new Token();
        token.setUserId(Integer.valueOf(myUserDetails.getId()));
        token.setToken(createAccessToken(authentication));
        return token;
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
