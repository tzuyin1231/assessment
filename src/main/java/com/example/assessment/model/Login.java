package com.example.assessment.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Login {
    String password;
    String phone;
    String token;
}
