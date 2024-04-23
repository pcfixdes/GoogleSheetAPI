package com.example.googlesheetapi.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.stereotype.Service;


@Service
public class JWTTokenValidationService {

    public boolean isValidToken(String token) {
        try {
            // Remove "Bearer " prefix if present
            String cleanedToken = token.replace("Bearer ", "");

            // Decode the token to verify structure
            DecodedJWT jwt = JWT.decode(cleanedToken);

            Algorithm algorithm = Algorithm.HMAC256("qwertyuiopasdfghjklzxcvbnm123456");

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("Carlos Torres")
                    .build();

            // Verify the token
            verifier.verify(cleanedToken);

            // Add additional validations if needed
            // For example, check if the token has expired
            if (jwt.getExpiresAt() != null && jwt.getExpiresAt().before(new java.util.Date())) {
                return false;
            }

            // The token is valid
            return true;
        } catch (JWTVerificationException exception) {
            // Token is not valid due to decoding error or verification failure
            return false;
        }
    }

}
