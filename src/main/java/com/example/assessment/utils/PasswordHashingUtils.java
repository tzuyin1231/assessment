package com.example.assessment.utils;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class PasswordHashingUtils {
    //        copy from https://www.baeldung.com/sha-256-hashing-java
    public static String getSha256hex(String password){
        return Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
    }
}
