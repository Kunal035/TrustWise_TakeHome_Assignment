package com.trustwise.utils;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Singleton
public class DBUtils {
    private static final DBUtils instance = new DBUtils();
    // Map of tables -> columns -> id -> data
    // tableNames -> columnNames -> id -> data
    // example: evaluation_logs -> id -> 1 -> "inputText"
    // example: evaluation_label_scores -> evaluation_log_id -> 1 -> { "label1": 0.5, "label2": 0.3 }
    private final Map<String, Map<String, Map<String, Object>>> internalDB;
    private long currentId = 0;

    private DBUtils() {
        internalDB = new HashMap<>();
        internalDB.put("evaluation_logs", new HashMap<>());
        internalDB.put("evaluation_label_scores", new HashMap<>());
    }

    public static DBUtils getInstance() {
        return instance;
    }

    private synchronized long insertData(String table, String column, String mp) {
        if (internalDB.containsKey(table) && internalDB.get(table).containsKey(column)) {
            // a loop such that it checks if there is a value that matches with mp and if it does then return the id
            // if it does not then return the id that is incremented by 1
            for (Map.Entry<String, Object> entry : internalDB.get(table).get(column).entrySet()) {
                if (entry.getValue().equals(mp)) {
                    return Long.parseLong(entry.getKey());
                }
            }
            Map<String, String> data = new HashMap<>();
            data.put(String.valueOf(getNextId()), mp);
            internalDB.get(table).get(column).putAll(data);
            return currentId;
        } else {
            Map<String, String> data = new HashMap<>();
            data.put(String.valueOf(getNextId()), mp);
            internalDB.computeIfAbsent(table, k -> new HashMap<>())
                    .computeIfAbsent(column, k -> new HashMap<>())
                    .putAll(data);
            return currentId;
        }
    }

    public synchronized long insertEvaluationLog(String inputText) {
        return insertData("evaluation_logs", "id", inputText);
    }
    public synchronized void insertEvaluationScores(Long id, Map<String, Object> scores) {
        insertScores(id, scores);
    }
    private synchronized void insertScores(Long id, Map<String, Object> scores) {
        Map<String, Object> data = new HashMap<>();
        for (Map.Entry<String, Object> entry : scores.entrySet()) {
            data.put(entry.getKey(), entry.getValue());
        }
        if(internalDB.containsKey("evaluation_label_scores") && internalDB.get("evaluation_label_scores").containsKey("evaluation_log_id")) {
            if(internalDB.get("evaluation_label_scores").get("evaluation_log_id").containsKey(String.valueOf(id))) {
                    Map<String, Object> alreadyPresentData = (Map<String, Object>) internalDB.get("evaluation_label_scores").get("evaluation_log_id").get(String.valueOf(id));
                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        alreadyPresentData.put(entry.getKey(), entry.getValue());
                    }
            } else {
                internalDB.get("evaluation_label_scores").get("evaluation_log_id").put(String.valueOf(id), data);
            }
        } else {
            internalDB.computeIfAbsent("evaluation_label_scores", k -> new HashMap<>())
                    .computeIfAbsent("evaluation_log_id", k -> new HashMap<>())
                    .put(String.valueOf(id), data);
        }
    }

    public synchronized Map<Long, Object> fetchInputTextById(Long id) {
        Map<Long, Object> result = new TreeMap<>();
        String idStr = String.valueOf(id);

        Map<String, Object> logsTable = internalDB.getOrDefault("evaluation_logs", Collections.emptyMap()).get("id");
        if (logsTable != null && logsTable.containsKey(idStr)) {
            result.put(id, logsTable.get(idStr));
        }
        return result;
    }

    public synchronized Map<Long, Map<String, Map<String, Double>>> fetchAllInputTextWithScores() {
        Map<Long, Map<String, Map<String, Double>>> result = new TreeMap<>();

        Map<String, Map<String, Object>> logsTable = internalDB.get("evaluation_logs");
        if (logsTable != null && logsTable.containsKey("id")) {
            for (String idStr : logsTable.get("id").keySet()) {
                Long id = Long.parseLong(idStr);
                String inputText = (String) logsTable.get("id").get(idStr);

                Map<String, Map<String, Object>> scoresTable = internalDB.get("evaluation_label_scores");
                if (scoresTable != null) {
                    if (scoresTable.get("evaluation_log_id").containsKey(idStr) && scoresTable.get("evaluation_log_id").get(idStr) != null) {
                        HashMap<String, Object> tempMap = (HashMap<String, Object>) scoresTable.get("evaluation_log_id").get(idStr);
                        Map<String, Double> scores = new HashMap<>();
                        for (Map.Entry<String, Object> entry : tempMap.entrySet()) {
                            scores.put(entry.getKey(), (Double) entry.getValue());
                        }
                        result.put(id, Collections.singletonMap(inputText, scores));
                    }
                } else {
                    result.put(id, Collections.singletonMap(inputText, new HashMap<>()));
                }
            }
        }

        return result;
    }

    public synchronized Map<Long, Object> fetchScoresById(Long id) {
        Map<Long, Object> result = new HashMap<>();
        String idStr = String.valueOf(id);

        Map<String, Map<String, Object>> scoresTable = internalDB.get("evaluation_label_scores");
        if (scoresTable != null && scoresTable.containsKey("evaluation_log_id") && scoresTable.get("evaluation_log_id").containsKey(idStr)) {
            result.put(id, scoresTable.get("evaluation_log_id").get(idStr));
        }

        return result;
    }

    public synchronized void deleteData(String table, String column, String key) {
        if (internalDB.containsKey(table) && internalDB.get(table).containsKey(column)) {
            internalDB.get(table).get(column).remove(key);
        }
    }

    private synchronized long getNextId() {
        return ++currentId;
    }
}