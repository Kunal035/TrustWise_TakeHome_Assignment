package com.trustwise.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HuggingFaceAPIClientEmotionalModel {

    private final static Logger LOGGER = LoggerFactory.getLogger(HuggingFaceAPIClientEmotionalModel.class);

    private static final String API_URL = System.getenv("API_URL");
    private static final String API_KEY = System.getenv("API_KEY");

    public String callEmotionModel(String inputText) {
        StringBuilder output = new StringBuilder();
        try {
            String curlCommand = String.format(
                    "curl --location '%s' --header 'Content-Type: application/json' --header 'Authorization: Bearer %s' --data '%s'",
                    API_URL, API_KEY, inputText);

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("bash", "-c", curlCommand);
            Process process = processBuilder.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

        } catch (Exception e) {
            LOGGER.error("Error executing curl command", e);
        }
        return output.toString();
    }
}
