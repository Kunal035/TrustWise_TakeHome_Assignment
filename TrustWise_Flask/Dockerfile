FROM pytorch/pytorch:latest as flask-build

WORKDIR /flask_app

COPY . .

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

RUN pip install --no-cache-dir flask-cors

RUN pip install --no-cache-dir flask==2.3.2
RUN pip install --no-cache-dir transformers==4.33.0

EXPOSE 5000

CMD ["python", "flask_app.py"]

