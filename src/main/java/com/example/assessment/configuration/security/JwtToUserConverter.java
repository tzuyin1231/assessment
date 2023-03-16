//package com.example.assessment.configuration.security;
//
////注意不要引用錯包
//import com.example.assessment.model.User;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//
//@Component
//public class JwtToUserConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {
//    @Override
//    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
//        MyUserDetails myUserDetails = new MyUserDetails();
//        myUserDetails.setId(jwt.getSubject());
//        return new UsernamePasswordAuthenticationToken(myUserDetails, jwt, Collections.EMPTY_LIST);
//
//    }
//}
