import React, { useState } from "react";
import axios from "axios";
import LogsTable from "./LogsTable";
import ChartComponent from "./ChartComponent"; 
import "./App.css";

function App() {
    const [inputText, setInputText] = useState(""); // Text input
    const [error, setError] = useState("");
    const [logs, setLogs] = useState([]);

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError("");

        if (!inputText.trim()) {
            setError("Please enter some text.");
            return;
        }

        try {
            const eduPayload = { text: inputText };
            const eduResponse = await axios.post(
                "http://localhost:5000/education",
                eduPayload,
                {
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );
            console.log(eduResponse.data.logits);
            // Step 1: "educ" API
            const eduData = eduResponse.data.logits;
            const eduPayload2 = { logits: eduData, text: inputText };
            const eduResponse2 = await axios.post(
                "http://localhost:8080/evaluation/log/educ",
                eduPayload2,
                {
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );

            // Step 2: "emotional" API
            const emotionalPayload = { inputs: inputText };
            await axios.post(
                "http://localhost:8080/evaluation/log/emotional",
                emotionalPayload,
                {
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );

            // Step 3: Fetch logs API
            const logsResponse = await axios.get("http://localhost:8080/evaluation/logs");

            setLogs(logsResponse.data);
            console.log(logsResponse.data);
        } catch (err) {
            setError("Failed to fetch the results. Please check your backend.");
        }
    };

    return (
        <div className="App">
            <h1>Evaluation Logs</h1>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    placeholder="Enter text for evaluation"
                    value={inputText}
                    onChange={(e) => setInputText(e.target.value)}
                />
                <button type="submit">Evaluate</button>
            </form>

            {error && <p className="error">{error}</p>}
            <LogsTable logs={logs} /> {}
            <ChartComponent logs={logs} /> {}
        </div>
    );
}

export default App;
