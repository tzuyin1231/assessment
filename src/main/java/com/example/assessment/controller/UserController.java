package com.example.assessment.controller;

import com.example.assessment.model.Article;
import com.example.assessment.model.User;
import com.example.assessment.repository.ArticleRepository;
import com.example.assessment.repository.UserRepository;
import com.example.assessment.utils.JwtUtils;
import com.example.assessment.utils.PasswordHashingUtils;
import com.google.common.hash.Hashing;
import jakarta.annotation.ManagedBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;


@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ArticleRepository articleRepository;

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
        user.setPassword(PasswordHashingUtils.getSha256hex(password));
        userRepository.save(user);
        return user;
    }

    @MutationMapping
    public User updateUser(
            @Argument Integer userId,
            @Argument String nickname,
            @Argument String phone
    ){
        User aimUser = userRepository.getById(userId);
        if(aimUser!=null){ //   如果有找到目標
            aimUser.setNickname(nickname);
            aimUser.setPhone(phone);
            userRepository.save(aimUser);
        }else{
            return null;
        }
        return aimUser;
    }

    @SchemaMapping
    public List<Article> articles(User user){
        return articleRepository.findAllByUserId(user.getUserId());
    }
}
