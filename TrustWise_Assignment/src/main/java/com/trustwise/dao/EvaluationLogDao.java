package com.trustwise.dao;

import com.trustwise.bean.EvaluationLog;
import com.trustwise.utils.DBUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluationLogDao implements EvaluationLogDaoInterface {

    DBUtils db = DBUtils.getInstance();

    @Override
    public EvaluationLog save(EvaluationLog log) {
        long logId = db.insertEvaluationLog(log.getInputText());
        log.setId(logId);

        Map<String, Object> map = new HashMap<>(log.getLabelScores());
        db.insertEvaluationScores(logId, map);

        System.out.println("Evaluation log saved successfully.");
        return log;
    }

    @Override
    public List<EvaluationLog> findAll() {
        Map<Long, Map<String, Map<String, Double>>> allLogs = db.fetchAllInputTextWithScores();
        List<EvaluationLog> logs = new ArrayList<>();

        for (Map.Entry<Long, Map<String, Map<String, Double>>> entry : allLogs.entrySet()) {
            EvaluationLog log = new EvaluationLog();
            log.setId(entry.getKey());
            log.setInputText(entry.getValue().keySet().iterator().next());
            log.setLabelScores(entry.getValue().values().iterator().next());
            logs.add(log);
        }

        return logs;
    }
    @Override
    public EvaluationLog findById(Long id) {
        Map<Long, Object> inputTextMap = db.fetchInputTextById(id);
        Map<Long, Object> scoresMap = db.fetchScoresById(id);

        if (inputTextMap.isEmpty() || scoresMap.isEmpty()) {
            return null;
        }

        EvaluationLog log = new EvaluationLog();
        log.setId(id);
        log.setInputText((String) inputTextMap.get(id));
        log.setLabelScores((Map<String, Double>) scoresMap.get(id));

        return log;
    }

    @Override
    public void delete(Long id) {
        db.deleteData("evaluation_logs", "id", String.valueOf(id));
        db.deleteData("evaluation_label_scores", "evaluation_log_id", String.valueOf(id));
        System.out.println("Evaluation log deleted successfully.");
    }

}