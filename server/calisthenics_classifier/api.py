from flask import Flask, request, jsonify
import logging.config
from train import Model

logging.config.dictConfig({
    "formatters": {
        "json": {
            "()": "jsonlogging.JSONFormatter",
        }
    },
    "filters": {
        "context": {
            "()": "pylogctx.AddContextFilter",
        }
    },
    "handlers": {
        "console": {
            "class": "logging.StreamHandler",
            "filters": ["context"],
            "formatter": "json",
        }
    },
    "root": {
        "level": "INFO",
        "handlers": ["console"],
    },
    "version": 1,
})

app = Flask(__name__)

model = Model(10)
model.load("/")


@app.route("/classify", methods=["GET"])
def classify():
    l = request.args.get("data", type=str)
    l = list(map(lambda x: float(x), l.split(",")))
    (x, y, z) = model.predict(l)
    return jsonify({
        "push": x,
        "pull": y,
        "other": z,
    })


@app.route("/health", methods=["GET"])
def health():
    return "OK"
