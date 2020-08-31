#!/bin/sh

set -ex

exec gunicorn \
    --log-config ./logging.conf --log-level "$LOG_LEVEL" \
    --timeout 7200 \
    --graceful-timeout 180 \
    --worker-class "gthread" --workers 1 --threads 1 \
    --bind 0.0.0.0:5001 \
    --limit-request-line 0 \
    api:app

# vim: tabstop=8 expandtab shiftwidth=4 softtabstop=4 fenc=utf-8
