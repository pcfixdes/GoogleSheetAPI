package com.example.googlesheetapi.config;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GoogleSheetsConfig {

    @Bean
    public Sheets sheetsService() throws GeneralSecurityException, IOException {
        // Set up HTTP transport and JSON factory
        final HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance(); // Using GsonFactory from com.google.api-client

        // Get the project's root directory
        String projectRoot = System.getProperty("user.dir");

        // Construct the file path relative to the project root
        String filePath = Paths.get(projectRoot, "", "google.json").toString();

        // Load service account credentials from the file
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(filePath))
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        // Build the Sheets service
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName("Google Sheets API")
                .build();
    }
}

