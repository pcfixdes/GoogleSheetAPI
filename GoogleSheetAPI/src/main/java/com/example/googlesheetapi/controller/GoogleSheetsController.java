package com.example.googlesheetapi.controller;

import com.example.googlesheetapi.service.GoogleSheetsService;
import com.example.googlesheetapi.service.JWTTokenValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoogleSheetsController {
    @Autowired
    private JWTTokenValidationService jwtTokenValidationService;

    @Autowired
    private GoogleSheetsService googleSheetsService;

    @PostMapping("/test/sheets")
    public ResponseEntity<String> interactWithGoogleSheets(@RequestHeader("Authorization") String token) {
        if (!jwtTokenValidationService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        }

        googleSheetsService.interactWithGoogleSheets();

        return ResponseEntity.ok("Google Sheets interaction successful");
    }
}
