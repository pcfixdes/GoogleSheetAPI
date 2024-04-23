package com.example.googlesheetapi.service;


import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class GoogleSheetsService {

    @Autowired
    private Sheets sheetsService;

    public void interactWithGoogleSheets() {
        try {
            // Step 1: Copy the spreadsheet
            String spreadsheetId = "1hLgSCS0VbEbkkhkeFLFdzzUrs4nD9eJ5KFHBVJZmD1I";
            Spreadsheet response = sheetsService.spreadsheets().get(spreadsheetId).execute();
            String newSpreadsheetId = copySpreadsheet(response);

            // Step 2: Add background color to cells with country "United States"
            addBackgroundColor(newSpreadsheetId);

            // Step 3: Add a new row with specified details
            addNewRow(newSpreadsheetId);

            // Step 4: Delete duplicate rows
            deleteDuplicateRows(newSpreadsheetId);

        } catch (IOException  e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    private String copySpreadsheet(Spreadsheet spreadsheet) throws IOException {
        // Get the title of the original spreadsheet
        String originalTitle = spreadsheet.getProperties().getTitle();

        // Create a new spreadsheet with the same title
        Spreadsheet requestBody = new Spreadsheet()
                .setProperties(new SpreadsheetProperties().setTitle(originalTitle + " - Copy"));
        Sheets.Spreadsheets.Create request = sheetsService.spreadsheets().create(requestBody);
        Spreadsheet response = request.execute();

        // Return the ID of the copied spreadsheet
        return response.getSpreadsheetId();
    }


    private void addBackgroundColor(String spreadsheetId) throws IOException {
        // Define the range to apply the background color
        String range = "A1:Z100"; // Adjust range as needed

        // Define the cell background color to be applied (Blue for "United States")
        Color blueColor = new Color()
                .setBlue(1f)
                .setGreen(1f)
                .setRed(0f);

        // Define the rule to apply the background color
        ConditionalFormatRule rule = new ConditionalFormatRule()
                .setRanges(Collections.singletonList(new GridRange().setSheetId(0).setStartRowIndex(0).setEndRowIndex(100)))
                .setBooleanRule(new BooleanRule()
                        .setCondition(new BooleanCondition()
                                .setType("CUSTOM_FORMULA")
                                .setValues(Collections.singletonList(new ConditionValue().setUserEnteredValue("=B1=\"United States\""))))
                        .setFormat(new CellFormat().setBackgroundColor(blueColor)));

        // Add the rule to the spreadsheet
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(Collections.singletonList(new Request()
                        .setAddConditionalFormatRule(new AddConditionalFormatRuleRequest().setRule(rule))));
        sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
    }


    private void addNewRow(String spreadsheetId) throws IOException {
        // Define the new row data
        List<Object> rowData = Arrays.asList("AODocs", "California", "United States", "(310) 310-1234");

        // Append the new row to the spreadsheet
        ValueRange requestBody = new ValueRange().setValues(Collections.singletonList(rowData));
        sheetsService.spreadsheets().values()
                .append(spreadsheetId, "Sheet1", requestBody)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }

    private void deleteDuplicateRows(String spreadsheetId) throws IOException {
        // Get all the values from the spreadsheet
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, "Sheet1")
                .execute();
        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            // No data to delete
            return;
        }

        // Identify duplicate rows and their indices
        Set<List<Object>> uniqueRows = new HashSet<>();
        List<Integer> rowsToDelete = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            List<Object> row = values.get(i);
            if (!uniqueRows.add(row)) {
                // Duplicate row found
                rowsToDelete.add(i + 1); // Adjust row index (1-based)
            }
        }

        if (!rowsToDelete.isEmpty()) {
            // Delete duplicate rows
            BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                    .setRequests(Collections.singletonList(new Request()
                            .setDeleteDimension(new DeleteDimensionRequest()
                                    .setRange(new DimensionRange()
                                            .setSheetId(0)
                                            .setDimension("ROWS")
                                            .setStartIndex(rowsToDelete.get(0) - 1) // Adjust start index (0-based)
                                            .setEndIndex(rowsToDelete.get(rowsToDelete.size() - 1))))));
            sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
        }
    }





}

