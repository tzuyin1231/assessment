package com.example.assessment.controller;

import com.example.assessment.configuration.security.ResourceServerConfig;
import com.example.assessment.configuration.security.TokenGenerator;
import com.example.assessment.model.LoginDTO;
import com.example.assessment.model.Token;
import com.example.assessment.model.User;
import com.example.assessment.repository.UserRepository;
import com.example.assessment.utils.JwtUtils;
import com.example.assessment.utils.PasswordHashingUtils;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@RestController
public class TokenController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenGenerator tokenGenerator;
//    @Autowired
//    DaoAuthenticationProvider daoAuthenticationProvider;

//    @RequestMapping("/token")
//    public String findById(@RequestBody Token token ){
//        System.out.println(token);
//        String phone = login.getPhone();
//        String hashInputPassword = PasswordHashingUtils.getSha256hex(login.getPassword());
//        User aimUser = userRepository.findByPhone(phone);
//        if(aimUser!=null){
//            if (hashInputPassword.contentEquals(aimUser.getPassword())){
//                String token = JwtUtils.generateJwtToken(aimUser);
//                return token;
//            }else{
//                return "wrong password";
//            }
//        }else{
//            return "cannot find the user";
//        }
//    }
//    @PostMapping("/login")
//    public ResponseEntity login(@RequestBody LoginDTO loginDTO) {
//        Authentication authentication = daoAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.getUsername(), loginDTO.getPassword()));
//        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
//    }
    @RequestMapping("/token")
    public String generateToken(){
        Instant now = Instant.now();
        /**
         * 生成JWT
         */
        System.out.println(ResourceServerConfig.key);
            JwtBuilder builder = Jwts.builder()
                    .setIssuedAt(Date.from(now)) // 簽發時間
                    .signWith(SignatureAlgorithm.HS256, ResourceServerConfig.key) // 金鑰
                    // 設置過期時間
                    .setExpiration(Date.from(now.plus(5, ChronoUnit.MINUTES)));
            return builder.compact();// 最後使用compact() 進行生成
    }
}
