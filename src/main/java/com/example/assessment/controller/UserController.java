package com.example.assessment.controller;

import com.example.assessment.model.Article;
import com.example.assessment.model.User;
import com.example.assessment.repository.ArticleRepository;
import com.example.assessment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    public BCryptPasswordEncoder bCryptPasswordEncoder;

    @QueryMapping
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @MutationMapping
    public User addNewUser(@Argument String nickname, @Argument String phone, @Argument String password) {
        User user = new User();
        user.setNickname(nickname);
        user.setPhone(phone);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    @MutationMapping
    public User updateUser(
            @Argument Integer userId,
            @Argument String nickname,
            @Argument String phone
    ) {
        User aimUser = userRepository.findById(userId).orElse(null);
        if (aimUser != null) {
            aimUser.setNickname(nickname);
            aimUser.setPhone(phone);
            userRepository.save(aimUser);
        } else {
            return null;
        }
        return aimUser;
    }

    @SchemaMapping
    public List<Article> articles(User user) {
        return articleRepository.findByUser(user);
    }
}
