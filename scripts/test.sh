#!/usr/bin/env sh
set -eu

if [ -d backend ]; then
  (cd backend && ./gradlew test)
fi

if [ -d frontend ]; then
  (cd frontend && npm run type-check && npm run test)
fi
