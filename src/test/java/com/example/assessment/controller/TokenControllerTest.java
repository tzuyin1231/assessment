package com.example.assessment.controller;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final Logger log = LoggerFactory.getLogger(TokenControllerTest.class);

    @Test
    void generateLongToken() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(get("/token/long"))
                .andExpect(status().isOk())
                .andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();
        log.info("token value should be {}", mvcResultString);
    }

}