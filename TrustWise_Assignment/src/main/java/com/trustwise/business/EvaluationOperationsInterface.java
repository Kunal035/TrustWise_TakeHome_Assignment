package com.trustwise.business;

import com.trustwise.bean.EvaluationLog;

import java.util.List;

public interface EvaluationOperationsInterface {
    EvaluationLog createEvaluationLogEmotional(String inputText);

    EvaluationLog createEvaluationLogEducation(String inputText);

    List<EvaluationLog> getAllEvaluationLogs();

    EvaluationLog getEvaluationLogById(Long id);

    void deleteEvaluationLog(Long id);
}