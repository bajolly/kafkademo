#!/bin/zsh

# Show messages on a Kafka topic
# Usage: ./bin/show-messages.sh <topic-name>
# Example: ./bin/show-messages.sh rss-items-topic

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

echo "Consuming messages from topic: $TOPIC_NAME"
echo "Broker: $BROKER_URL"
echo "Press Ctrl+C to stop..."
echo "=========================================="

kafka-console-consumer.sh --topic $TOPIC_NAME --from-beginning --bootstrap-server $BROKER_URL