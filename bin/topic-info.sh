#!/bin/zsh

# Show detailed information about a Kafka topic
# Usage: ./bin/topic-info.sh <topic-name>
# Example: ./bin/topic-info.sh rss-items-topic

set -e

# Check if topic name is provided
if [ -z "$1" ]; then
    echo "Error: Topic name is required"
    echo "Usage: $0 <topic-name>"
    echo "Example: $0 rss-items-topic"
    exit 1
fi

TOPIC_NAME=$1
BROKER_URL=${BROKER_URL:-localhost:9092}

echo "Topic Information for: $TOPIC_NAME"
echo "Broker: $BROKER_URL"
echo "=================================="

kafka-topics.sh --describe --topic $TOPIC_NAME --bootstrap-server $BROKER_URL