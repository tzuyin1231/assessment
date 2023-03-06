package com.example.assessment.controller;

import com.example.assessment.model.Login;
import com.example.assessment.model.User;
import com.example.assessment.repository.UserRepository;
import com.example.assessment.utils.JwtUtils;
import com.example.assessment.utils.PasswordHashingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @Autowired
    UserRepository userRepository;

    @RequestMapping("/users")
    public String findById(@RequestBody Login login){
        System.out.println(login);
        String phone = login.getPhone();
        String hashInputPassword = PasswordHashingUtils.getSha256hex(login.getPassword());
        User aimUser = userRepository.findByPhone(phone);
        if(aimUser!=null){
            if (hashInputPassword.contentEquals(aimUser.getPassword())){
                String token = JwtUtils.generateJwtToken(aimUser);
                return token;
            }else{
                return "wrong password";
            }
        }else{
            return "cannot find the user";
        }

    }

}
