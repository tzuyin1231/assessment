package com.example.assessment.model;

import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FooTest {
    @Test
    public void givenUserWithReadScope_whenGetFooResource_thenSuccess() {
        String accessToken = obtainAccessToken("read");

        Response response = RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .get("http://localhost:8081/resource-server-jwt/foos");
        assertThat(response.as(List.class)).hasSizeGreaterThan(0);
    }
}