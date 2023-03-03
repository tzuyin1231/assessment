package com.example.assessment.controller;

import com.example.assessment.model.User;
import com.example.assessment.repository.UserRepository;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.nio.charset.StandardCharsets;
import java.util.List;


@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;

    @QueryMapping
    public List<User> findAllUsers(){
        System.out.println("hi");
        return userRepository.findAll();
    }

    @MutationMapping
    public User addNewUser(@Argument String nickname, @Argument String phone, @Argument String password){
        User user = new User();
        user.setNickname(nickname);
        user.setPhone(phone);
//        copy from https://www.baeldung.com/sha-256-hashing-java
        String sha256hex = Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
        user.setPassword(sha256hex);
        userRepository.save(user);
        return user;
    }
}
