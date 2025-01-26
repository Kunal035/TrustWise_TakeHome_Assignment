import React, { useEffect, useRef } from "react";
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
    BarController,
} from "chart.js";
import ChartDataLabels from "chartjs-plugin-datalabels";

ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
    BarController,
    ChartDataLabels
);

const ChartComponent = ({ logs }) => {
    const chartRef = useRef(null);
    const chartInstanceRef = useRef(null);

    useEffect(() => {
        if (logs.length === 0) {
            console.log("No logs available to display chart.");
            return;
        }

        console.log("Rendering chart with logs:", logs);

        if (chartRef.current) {
            const ctx = chartRef.current.getContext("2d");

            if (chartInstanceRef.current) {
                chartInstanceRef.current.destroy();
            }
            const reversedLogs = [...logs].reverse();
            const groupedLogs = {};
            reversedLogs.forEach((log) => {
                const logText = JSON.parse(log.inputText).text || JSON.parse(log.inputText).inputs;

                if (!groupedLogs[logText]) {
                    groupedLogs[logText] = {
                        education: null,
                        emotion: { label: null, value: null },
                    };
                }

                const labelScores = log.labelScores;
                Object.keys(labelScores).forEach((key) => {
                    if (key === "education") {
                        groupedLogs[logText].education = labelScores[key];
                    } else {
                        groupedLogs[logText].emotion.label = key;
                        groupedLogs[logText].emotion.value = labelScores[key];
                    }
                });
                console.log(groupedLogs)
            });
            
            const labels = Object.keys(groupedLogs); // X-axis labels
            const educationData = labels.map((label) => groupedLogs[label].education || 0);
            const emotionData = labels.map((label) => groupedLogs[label].emotion.value || 0);
            const emotionLabels = labels.map((label) => groupedLogs[label].emotion.label || "Unknown");

            // Create the chart
            chartInstanceRef.current = new ChartJS(ctx, {
                type: "bar",
                data: {
                    labels: labels,
                    datasets: [
                        {
                            label: "Education",
                            data: educationData,
                            backgroundColor: "rgba(255, 99, 132, 0.5)",
                            borderColor: "rgba(255, 99, 132, 1)",
                            borderWidth: 1,
                            datalabels: {
                                anchor: "end",
                                align: "end",
                                formatter: function (value) {
                                    return "Education";
                                },
                                font: {
                                    weight: "bold",
                                    size: 11,

                                },
                                color: "rgb(152,29,118)",
                            },
                        },
                        {
                            label: "Emotion",
                            data: emotionData,
                            backgroundColor: "rgba(75, 192, 192, 0.5)",
                            borderColor: "rgba(75, 192, 192, 1)",
                            borderWidth: 1,
                            datalabels: {
                                anchor: "end",
                                align: "end",
                                formatter: function (value, context) {
                                    const index = context.dataIndex;
                                    return emotionLabels[index];
                                },
                                font: {
                                    weight: "bold",
                                    size: 11,
                                },
                                color: "rgb(56,163,234)",
                            },
                        },
                    ],
                },
                options: {
                    responsive: true,
                    plugins: {
                        tooltip: {
                            callbacks: {
                                label: function (tooltipItem) {
                                    const datasetIndex = tooltipItem.datasetIndex;
                                    const index = tooltipItem.dataIndex;
                                    if (datasetIndex === 1) {
                                        return `Emotion (${emotionLabels[index]}): ${tooltipItem.raw}`;
                                    }
                                    return `Education: ${tooltipItem.raw}`;
                                },
                            },
                        },
                        title: {
                            display: true,
                            text: "Grouped Bar Chart (Education and Emotion)",
                        },
                        datalabels: {
                            display: true,
                        },
                    },
                    scales: {
                        x: {
                            beginAtZero: true,
                            stacked: false,
                            ticks: {
                                color: "black",
                                font: {
                                    weight: "bold",
                                },
                            },
                            barPercentage: 0.75,
                            categoryPercentage: 1.5,
                        },
                        y: {
                            beginAtZero: true,
                            max: 1.1,
                            ticks: {
                                color: "black",
                                font: {
                                    weight: "bold",
                                },
                            },
                        },
                    },
                },
                plugins: [ChartDataLabels],
            });
        }
    }, [logs]);

    return (
        <div style={{ width: "80%", height: "400px", margin: "0 auto" }}>
            <h2>Chart Component</h2>
            <canvas ref={chartRef}></canvas>
        </div>
    );
};

export default ChartComponent;
