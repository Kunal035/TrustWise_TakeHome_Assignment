package com.trustwise.bean;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "evaluation_logs")
public class EvaluationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String inputText;

    @ElementCollection
    @CollectionTable(name = "evaluation_label_scores", joinColumns = @JoinColumn(name = "evaluation_log_id"))
    private Map<String, Double> labelScores;

    public EvaluationLog() {}

    public EvaluationLog(String inputText, Map<String, Double> labelScores) {
        this.inputText = inputText;
        this.labelScores = labelScores;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public Map<String, Double> getLabelScores() {
        return labelScores;
    }

    public void setLabelScores(Map<String, Double> labelScores) {
        this.labelScores = labelScores;
    }
}
