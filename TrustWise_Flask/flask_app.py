from flask import Flask, request, jsonify
from transformers import AutoTokenizer, AutoModelForSequenceClassification
import torch
from flask_cors import CORS

app = Flask(__name__)
CORS(app) 
print("Loading the education model...")
education_model_name = "HuggingFaceTB/fineweb-edu-classifier"
education_tokenizer = AutoTokenizer.from_pretrained(education_model_name)
education_model = AutoModelForSequenceClassification.from_pretrained(education_model_name)
print("Education model loaded successfully!")

@app.route('/')
def home():
    return "Flask server is running and ready!"

@app.route('/education', methods=['POST'])
def predict_education():
    try:
        data = request.get_json()
        text = data.get("text", "")

        if not text:
            return jsonify({"error": "Input text is empty"}), 400

        inputs = education_tokenizer(text, return_tensors="pt", padding="longest", truncation=True)
        outputs = education_model(**inputs)

        logits = outputs.logits.squeeze().item()
        return jsonify({"logits": logits})
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
