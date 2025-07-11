#!/bin/zsh

# List all Kafka topics
# Usage: ./bin/list-topics.sh

set -e

# Default broker URL
BROKER_URL=${BROKER_URL:-localhost:9092}

echo "Listing Kafka topics on broker: $BROKER_URL"
echo "============================================"

kafka-topics.sh --list --bootstrap-server $BROKER_URL