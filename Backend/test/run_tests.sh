#!/bin/sh

if [ "$#" -ge 1 ] && [ "$1" = "--stdout" ]; then
  python3 -m pytest -s -v "${FLASKTEST_PATH}"
elif [ "$#" -ge 1 ] && [ "$1" = "--cov" ]; then
  python3 -m pytest -v --cov --cov-report=xml:/tmp/coverage.xml "${FLASKTEST_PATH}"
else
  python3 -m pytest -v "${FLASKTEST_PATH}"
fi