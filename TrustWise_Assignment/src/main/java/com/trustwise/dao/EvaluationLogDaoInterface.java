package com.trustwise.dao;

import com.trustwise.bean.EvaluationLog;

import java.util.List;

public interface EvaluationLogDaoInterface {

    EvaluationLog save(EvaluationLog log);

    List<EvaluationLog> findAll();

    EvaluationLog findById(Long id);

    void delete(Long id);
}
