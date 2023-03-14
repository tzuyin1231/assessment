//package com.example.assessment.model;
//
//
//import com.example.assessment.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserService {
//    @Autowired
//    private UserRepository repo;
//
//
//    public void processOAuthPostLogin(String phone) {
//        User existUser = repo.findByPhone(phone);
//
//        if (existUser == null) {
//            User newUser = new User();
//            newUser.setPhone(phone);
//            repo.save(newUser);
//        }
//
//    }
//}
