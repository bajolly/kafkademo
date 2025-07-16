#!/bin/bash

# Function to kill existing processes
cleanup_existing_processes() {
    echo "Checking for existing Kafka and Zookeeper processes..."
    
    # Kill existing Kafka processes
    KAFKA_PIDS=$(pgrep -f "kafka.Kafka" | grep -v grep)
    if [ ! -z "$KAFKA_PIDS" ]; then
        echo "Found existing Kafka processes: $KAFKA_PIDS"
        echo "Stopping Kafka..."
        kill -TERM $KAFKA_PIDS 2>/dev/null
        sleep 2
        # Force kill if still running
        kill -9 $KAFKA_PIDS 2>/dev/null
    fi
    
    # Kill existing Zookeeper processes
    ZK_PIDS=$(pgrep -f "org.apache.zookeeper.server.quorum.QuorumPeerMain" | grep -v grep)
    if [ ! -z "$ZK_PIDS" ]; then
        echo "Found existing Zookeeper processes: $ZK_PIDS"
        echo "Stopping Zookeeper..."
        kill -TERM $ZK_PIDS 2>/dev/null
        sleep 2
        # Force kill if still running
        kill -9 $ZK_PIDS 2>/dev/null
    fi
    
    # Wait a bit for processes to fully terminate
    sleep 3
}

# Clean up any existing processes
cleanup_existing_processes

# Start Zookeeper in the background
echo "Starting Zookeeper..."
zookeeper-server-start.sh ~/kafka_2.13-3.9.0/config/zookeeper.properties &
ZOOKEEPER_PID=$!

# Wait for Zookeeper to be ready
echo "Waiting for Zookeeper to start..."
MAX_WAIT=30
WAIT_COUNT=0

while ! nc -z localhost 2181 2>/dev/null; do
    if [ $WAIT_COUNT -gt $MAX_WAIT ]; then
        echo "Error: Zookeeper failed to start within $MAX_WAIT seconds"
        exit 1
    fi
    echo "Zookeeper not ready yet. Waiting..."
    sleep 1
    ((WAIT_COUNT++))
done

echo "Zookeeper is ready!"

# Start Kafka
echo "Starting Kafka..."
kafka-server-start.sh ~/kafka_2.13-3.9.0/config/server.properties &
KAFKA_PID=$!

# Wait a bit for Kafka to start
sleep 5

# Check if Kafka started successfully
if ps -p $KAFKA_PID > /dev/null; then
    echo "Kafka started successfully (PID: $KAFKA_PID)"
else
    echo "Error: Kafka failed to start"
    exit 1
fi
