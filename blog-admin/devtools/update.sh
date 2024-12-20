#!/usr/bin/env bash
# 引数の数をチェック
if [ "$#" -ne 1 ]; then
  echo "Usage: $0 <ENTRY_PATH>"
  echo "Example: $0 2024/12/19/152345"
  exit 1
fi

ENTRY_PATH=$1
ENCODED_PATH=$(echo "$ENTRY_PATH" | jq -sRr @uri)

curl -X PATCH http://localhost:5173/api/entry/$ENCODED_PATH \
-H "Content-Type: application/json" \
-d '{
  "title": "Update Test Entry",
  "body": "This entry was updated!",
  "status": "draft"
}'
