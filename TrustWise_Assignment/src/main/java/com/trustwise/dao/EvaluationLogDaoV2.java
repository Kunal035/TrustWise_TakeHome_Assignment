package com.trustwise.dao;

import com.trustwise.bean.EvaluationLog;
import com.trustwise.utils.DBUtilsV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class EvaluationLogDaoV2 implements EvaluationLogDaoInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationLogDaoV2.class);

    @Override
    public EvaluationLog save(EvaluationLog log) {
        String insertLogSql = "INSERT INTO evaluation_logs (input_text) VALUES (?)";
        String insertLabelScoresSql = "INSERT INTO evaluation_label_scores (evaluation_log_id, label, score) VALUES (?, ?, ?)";

        try (Connection conn = DBUtilsV2.getConnection();
             PreparedStatement insertLogStmt = conn.prepareStatement(insertLogSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement insertLabelScoresStmt = conn.prepareStatement(insertLabelScoresSql)) {

            // Insert the evaluation log into the evaluation_logs table
            insertLogStmt.setString(1, log.getInputText());
            int affectedRows = insertLogStmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = insertLogStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long logId = generatedKeys.getLong(1);
                        log.setId(logId);  // Set the generated ID to the log

                        // Insert each label and its score into the evaluation_label_scores table
                        for (Map.Entry<String, Double> entry : log.getLabelScores().entrySet()) {
                            insertLabelScoresStmt.setLong(1, logId);
                            insertLabelScoresStmt.setString(2, entry.getKey());
                            insertLabelScoresStmt.setDouble(3, entry.getValue());
                            insertLabelScoresStmt.addBatch(); // Add to batch for batch insert
                        }

                        // Execute batch insert for label-score pairs
                        insertLabelScoresStmt.executeBatch();
                        LOGGER.info("Evaluation log saved successfully.");
                        return log;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error saving evaluation log.", e);
        }
        return null;
    }

    @Override
    public List<EvaluationLog> findAll() {
        String sql = "SELECT * FROM evaluation_logs";
        String labelSql = "SELECT evaluation_log_id, label, score FROM evaluation_label_scores WHERE evaluation_log_id IN (SELECT id FROM evaluation_logs)";
        List<EvaluationLog> logs = new ArrayList<>();
        Map<Long, EvaluationLog> logMap = new HashMap<>();

        try (Connection conn = DBUtilsV2.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             PreparedStatement labelStmt = conn.prepareStatement(labelSql);
             ResultSet rs = pstmt.executeQuery();
             ResultSet labelRs = labelStmt.executeQuery()) {

            while (rs.next()) {
                EvaluationLog log = new EvaluationLog();
                log.setId(rs.getLong("id"));
                log.setInputText(rs.getString("input_text"));
                logs.add(log);
                logMap.put(log.getId(), log);
            }

            while (labelRs.next()) {
                long logId = labelRs.getLong("evaluation_log_id");
                String label = labelRs.getString("label");
                double score = labelRs.getDouble("score");
                EvaluationLog log = logMap.get(logId);
                if (log != null) {
                    log.getLabelScores().put(label, score);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error finding all evaluation logs.", e);
        }
        return logs;
    }

    @Override
    public EvaluationLog findById(Long id) {
        String sql = "SELECT * FROM evaluation_logs WHERE id = ?";
        String labelSql = "SELECT label, score FROM evaluation_label_scores WHERE evaluation_log_id = ?";
        try (Connection conn = DBUtilsV2.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             PreparedStatement labelStmt = conn.prepareStatement(labelSql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    EvaluationLog log = new EvaluationLog();
                    log.setId(rs.getLong("id"));
                    log.setInputText(rs.getString("input_text"));

                    // Retrieve label-scores from the evaluation_label_scores table
                    Map<String, Double> labelScores = new HashMap<>();
                    labelStmt.setLong(1, log.getId());
                    try (ResultSet labelRs = labelStmt.executeQuery()) {
                        while (labelRs.next()) {
                            String label = labelRs.getString("label");
                            double score = labelRs.getDouble("score");
                            labelScores.put(label, score);
                        }
                    }

                    log.setLabelScores(labelScores);
                    return log;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error finding evaluation log by ID.", e);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        String deleteLogSql = "DELETE FROM evaluation_logs WHERE id = ?";
        String deleteLabelScoresSql = "DELETE FROM evaluation_label_scores WHERE evaluation_log_id = ?";
        try (Connection conn = DBUtilsV2.getConnection();
             PreparedStatement deleteLogStmt = conn.prepareStatement(deleteLogSql);
             PreparedStatement deleteLabelScoresStmt = conn.prepareStatement(deleteLabelScoresSql)) {

            // Delete label-scores first
            deleteLabelScoresStmt.setLong(1, id);
            deleteLabelScoresStmt.executeUpdate();

            // Then delete the log
            deleteLogStmt.setLong(1, id);
            int affectedRows = deleteLogStmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Evaluation log deleted successfully.");
            } else {
                LOGGER.warn("No log found with ID: {}", id);
            }
        } catch (SQLException e) {
            LOGGER.error("Error deleting evaluation log by ID.", e);
        }
    }
}