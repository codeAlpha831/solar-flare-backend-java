package com.solareflarebackend.service;

import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.office.OfficeException;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.incubator.search.TextNavigation;
import org.odftoolkit.odfdom.incubator.search.TextSelection;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Service
public class OdtToPdfService {

    private final DocumentConverter documentConverter;

    public OdtToPdfService(DocumentConverter documentConverter) {
        this.documentConverter = documentConverter;
    }

    public void convertOdtToPdf(String inputFilePath, String outputFilePath) throws OfficeException {
        File inputFile = new File(inputFilePath);
        File outputFile = new File(outputFilePath);
        documentConverter.convert(inputFile).to(outputFile).execute();
    }

    public void findAndReplaceTextInOdt(String inputFilePath, Map<String, String> replacements) throws IOException {
        try {
            // Load the ODT document
            OdfTextDocument textDocument = OdfTextDocument.loadDocument(new File(inputFilePath));

            // Perform text replacement for each pair
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                String placeholder =  entry.getKey() ;
                TextNavigation textNavigation = new TextNavigation(placeholder, textDocument);
                while (textNavigation.hasNext()) {
                    TextSelection item = (TextSelection) textNavigation.getCurrentItem();
                    item.replaceWith(entry.getValue());
                }
            }

            // Save the modified document
            textDocument.save(inputFilePath);

            System.out.println("Text replaced successfully in ODT file: " + inputFilePath);
        } catch (Exception e) {
            throw new IOException("Error replacing text in ODT file", e);
        }
    }

    public String createTemporaryOdtFile(String templateFilePath) throws IOException {
        Path templatePath = Path.of(templateFilePath);
        Path tempFilePath = templatePath.getParent().resolve("TempTemplate.odt");
        Files.copy(templatePath, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
        return tempFilePath.toString();
    }

    public void cleanupTemporaryFile(String tempFilePath) throws IOException {
        Files.deleteIfExists(Path.of(tempFilePath));
    }
}
