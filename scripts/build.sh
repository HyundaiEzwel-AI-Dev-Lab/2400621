#!/usr/bin/env sh
set -eu

if [ -d backend ]; then
  (cd backend && ./gradlew build)
fi

if [ -d frontend ]; then
  (cd frontend && npm run build)
fi
