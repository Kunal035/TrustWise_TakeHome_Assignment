import React from "react";
import "./LogsTable.css";

const LogsTable = ({ logs }) => {
    const reversedLogs = [...logs].reverse();
    const groupedLogs = [];

    reversedLogs.forEach((log) => {
        const logText = JSON.parse(log.inputText).text || JSON.parse(log.inputText).inputs;

        const existingLog = groupedLogs.find((entry) => entry.text === logText);

        if (existingLog) {
            existingLog.labelScores = {
                ...existingLog.labelScores,
                ...log.labelScores,
            };
        } else {
            groupedLogs.push({
                text: logText,
                labelScores: { ...log.labelScores },
            });
        }
    });

    const formatEmotionLabels = (labelScores) => {
        const emotionLabels = [];

        for (const label in labelScores) {
            if (label !== "education") {
                emotionLabels.push(`${label}: ${labelScores[label].toFixed(4)}`);
            }
        }

        return emotionLabels.length > 0 ? emotionLabels.join(", ") : "N/A";
    };

    return (
        <div className="logs-table-container">
            <h2>Logs History</h2>
            <table className="logs-table">
                <thead>
                <tr>
                    <th style={{ textAlign: "center" }}>Text</th>
                    <th style={{ textAlign: "center" }}>Emotion</th>
                    <th style={{ textAlign: "center" }}>Education</th>
                </tr>
                </thead>
                <tbody>
                {groupedLogs.map((log, index) => (
                    <tr key={index}>
                        <td>{log.text}</td>
                        <td>{formatEmotionLabels(log.labelScores)}</td>
                        <td>{log.labelScores.education ? log.labelScores.education.toFixed(4) : "N/A"}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default LogsTable;
