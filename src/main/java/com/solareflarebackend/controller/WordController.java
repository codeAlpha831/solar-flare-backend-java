package com.solareflarebackend.controller;

import com.solareflarebackend.service.OdtToPdfService;
import org.jodconverter.core.office.OfficeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/convert")
    public class WordController {

    @Autowired
    private OdtToPdfService odtToPdfService;

    @PostMapping("/odt-to-pdf")
    public String convertOdtToPdf(@RequestBody Map<String, String> replacements) {
        String tempFilePath = null;
        try {
            // Define file paths
            String templateFilePath = "wwwroot/FinalTemplate.odt";
            String outputFilePath = "wwwroot/Template.pdf";

            // Create a temporary copy of the template file in the same directory
            tempFilePath = odtToPdfService.createTemporaryOdtFile(templateFilePath);

            // Perform text replacement in the temporary ODT file
            odtToPdfService.findAndReplaceTextInOdt(tempFilePath, replacements);

            // Convert modified temporary ODT to PDF
            odtToPdfService.convertOdtToPdf(tempFilePath, outputFilePath);

            return "ODT to PDF conversion successful";
        } catch (IOException | OfficeException e) {
            e.printStackTrace();
            return "Error during ODT to PDF conversion: " + e.getMessage();
        } finally {
            if (tempFilePath != null) {
                try {
                    // Clean up the temporary file
                    odtToPdfService.cleanupTemporaryFile(tempFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
