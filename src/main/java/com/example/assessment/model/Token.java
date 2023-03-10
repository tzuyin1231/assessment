package com.example.assessment.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Token {
    Integer userId;
    String token;
}
