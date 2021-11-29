#!/bin/sh

if [ "$#" -ge 1 ] && [ "$1" = "--stdout" ]; then
  python3 -m pytest -s -v "${FLASKTEST_PATH}"
else
  python3 -m pytest -v "${FLASKTEST_PATH}"
fi