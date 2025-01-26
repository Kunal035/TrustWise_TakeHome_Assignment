package com.trustwise.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustwise.utils.HuggingFaceAPIClientEmotionalModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HuggingFaceHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HuggingFaceHelper.class);
    private static final HuggingFaceAPIClientEmotionalModel huggingFaceAPIClientEmotional = new HuggingFaceAPIClientEmotionalModel();

    public Map<String, Double> getLabelsAndScoresEmotional(String inputText) {
        if (inputText == null || inputText.isEmpty()) {
            LOGGER.error("Input text is null or empty for emotional evaluation.");
            throw new IllegalArgumentException("Input text cannot be null or empty.");
        }
        try {
            String response = huggingFaceAPIClientEmotional.callEmotionModel(inputText);
            return parseLabelScores(response);
        } catch (Exception e) {
            LOGGER.error("Error getting labels and scores for emotional evaluation.", e);
            throw new RuntimeException("An error occurred while getting labels and scores for emotional evaluation.", e);
        }
    }

    public Map<String, Object> processLabelsAndScoresEducational(String inputText) throws JsonProcessingException {
        if (inputText == null || inputText.isEmpty()) {
            LOGGER.error("Input text is null or empty for educational evaluation.");
            throw new IllegalArgumentException("Input text cannot be null or empty.");
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(inputText);
            double logits = rootNode.get("logits").asDouble();
            String input = rootNode.get("text").asText();
            Map<String, Object> labelScores = new HashMap<>();
            labelScores.put("education", logits);
            labelScores.put("inputText", input);
            return labelScores;
        } catch (Exception e) {
            LOGGER.error("Error getting labels and scores for educational evaluation.", e);
            throw new RuntimeException("An error occurred while getting labels and scores for educational evaluation.", e);
        }
    }

    private Map<String, Double> parseLabelScores(String response) {
        Map<String, Double> labelScores = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode labelArray = rootNode.get(0);

            String maxLabel = "";
            double maxScore = Double.NEGATIVE_INFINITY;

            for (JsonNode labelNode : labelArray) {
                String label = labelNode.get("label").asText();
                double score = labelNode.get("score").asDouble();
                if (score > maxScore) {
                    maxScore = score;
                    maxLabel = label;
                }
            }
            labelScores.put(maxLabel, maxScore);
        } catch (Exception e) {
            LOGGER.error("Error parsing label scores.", e);
        }
        return labelScores;
    }
}