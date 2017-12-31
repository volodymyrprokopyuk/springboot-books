#!/usr/bin/env bash

set -eu

SERVICE_HOST="http://localhost:8080"
SERVICE_PATH="/books"
SERVICE_BASE_URI="${SERVICE_HOST}${SERVICE_PATH}"
OAUTH_TOKEN_URI="${SERVICE_HOST}/oauth/token"

HEADER_ACCEPT_JSON="Accept: application/json;charset=UTF-8"
HEADER_ACCEPT_XML="Accept: application/xml;charset=UTF-8"
HEADER_CONTENT_TYPE_JSON="Content-Type: application/json;charset=UTF-8"

BOOK_JSON='{"title": "New Title"}'

oauth_get_access_token_client_credentials() {
    local client_credentials=${1:?"ERROR: client credentials are not provided"}
    curl -s -X POST "${OAUTH_TOKEN_URI}" -u "${client_credentials}" -d "grant_type=client_credentials" | jq -r '.access_token'
}

ACCESS_TOKEN=$(oauth_get_access_token_client_credentials "write_client:write_client")

curl -s -X POST "${SERVICE_BASE_URI}" -H "${HEADER_ACCEPT_JSON}" -H "${HEADER_CONTENT_TYPE_JSON}" \
     -H "Authorization: Bearer ${ACCESS_TOKEN}" -d "${BOOK_JSON}" | jq .

ACCESS_TOKEN=$(oauth_get_access_token_client_credentials "read_client:read_client")

curl -s -X GET "${SERVICE_BASE_URI}" -H "${HEADER_ACCEPT_JSON}" -H "Authorization: Bearer ${ACCESS_TOKEN}" | jq .

curl -s -X GET "${SERVICE_HOST}/user" -H "${HEADER_ACCEPT_JSON}" -H "Authorization: Bearer ${ACCESS_TOKEN}" | jq .
