package com.trustwise.business;

import com.trustwise.bean.EvaluationLog;
import com.trustwise.dao.EvaluationLogDao;
import com.trustwise.helper.HuggingFaceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class EvaluationOperations implements EvaluationOperationsInterface {

	private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationOperations.class);
	private final EvaluationLogDao evaluationLogDao;
	private final HuggingFaceHelper huggingFaceHelper;

	public EvaluationOperations() {
		evaluationLogDao = new EvaluationLogDao();
		huggingFaceHelper = new HuggingFaceHelper();
	}

	@Override
	public EvaluationLog createEvaluationLogEmotional(String inputText) {
		if (inputText == null || inputText.isEmpty()) {
			LOGGER.error("Input text is null or empty for emotional evaluation log.");
			throw new IllegalArgumentException("Input text cannot be null or empty.");
		}
		try {
			Map<String, Double> labelScores = huggingFaceHelper.getLabelsAndScoresEmotional(inputText);
			if (labelScores != null && !labelScores.isEmpty()) {
				EvaluationLog log = new EvaluationLog(inputText, labelScores);
				return evaluationLogDao.save(log);
			} else {
				LOGGER.error("Failed to get label scores for emotional evaluation log.");
				throw new RuntimeException("Failed to get label scores for emotional evaluation log.");
			}
		} catch (Exception e) {
			LOGGER.error("Error creating emotional evaluation log.", e);
			throw new RuntimeException("An error occurred while creating the emotional evaluation log.", e);
		}
	}

	@Override
	public EvaluationLog createEvaluationLogEducation(String inputText) {
		if (inputText == null || inputText.isEmpty()) {
			LOGGER.error("Input text is null or empty for educational evaluation log.");
			throw new IllegalArgumentException("Input text cannot be null or empty.");
		}
		try {
			Map<String, Object> labelScores = huggingFaceHelper.processLabelsAndScoresEducational(inputText);
			if (labelScores != null && !labelScores.isEmpty()) {
				Map<String, Double> scores = new HashMap<>();
				scores.put("education", (Double)labelScores.get("education"));
				String inputs = "{\"text\": \"" + labelScores.get("inputText") + "\"}";
				EvaluationLog log = new EvaluationLog(inputs, scores);
				return evaluationLogDao.save(log);
			} else {
				LOGGER.error("Failed to get label scores for educational evaluation log.");
				throw new RuntimeException("Failed to get label scores for educational evaluation log.");
			}
		} catch (Exception e) {
			LOGGER.error("Error creating educational evaluation log.", e);
			throw new RuntimeException("An error occurred while creating the educational evaluation log.", e);
		}
	}

	@Override
	public List<EvaluationLog> getAllEvaluationLogs() {
		try {
			return evaluationLogDao.findAll();
		} catch (Exception e) {
			LOGGER.error("Error fetching all evaluation logs.", e);
			throw new RuntimeException("An error occurred while fetching all evaluation logs.", e);
		}
	}

	@Override
	public EvaluationLog getEvaluationLogById(Long id) {
		try {
			return evaluationLogDao.findById(id);
		} catch (Exception e) {
			LOGGER.error("Error fetching evaluation log by ID.", e);
			throw new RuntimeException("An error occurred while fetching the evaluation log by ID.", e);
		}
	}

	@Override
	public void deleteEvaluationLog(Long id) {
		try {
			evaluationLogDao.delete(id);
		} catch (Exception e) {
			LOGGER.error("Error deleting evaluation log by ID.", e);
			throw new RuntimeException("An error occurred while deleting the evaluation log by ID.", e);
		}
	}
}