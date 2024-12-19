#!/usr/bin/env bash
curl -X POST http://localhost:5173/api/entry \
-H "Content-Type: application/json" \
-d '{
  "title": "Test Entry",
  "body": "This is a test entry body.",
  "status": "draft"
}'
