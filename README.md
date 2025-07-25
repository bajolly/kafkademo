# Kafka Demo

This project demonstrates the usage of Apache Kafka. 

## Prerequisites

Make sure you have an instance of Kafka running. You can start Kafka using the following steps:

1. **Download Kafka**: If you haven't already, download Kafka from the [official website](https://kafka.apache.org/downloads).
0
2. **Install Kafka**: Assuming you unzip Kafka to ${HOME}/kafka_2.13-3.9.0 ( your version may vary ).  Add the `bin` directory to the `$PATH` environmental variable.

3. **Start Zookeeper**: Kafka requires Zookeeper to manage its cluster. Start Zookeeper with the following command:
    ```sh
    zookeeper-server-start.sh ~/kafka_2.13-3.9.0/config/zookeeper.properties
    ```

4. **Start Kafka**: Once Zookeeper is running, start Kafka with the following command:
    ```sh
    kafka-server-start.sh ~/kafka_2.13-3.9.0/config/server.properties
    ```
5. **Start Required Services** The Demo Project expects ChromaDB to be running.  Start the service in docker by running 
    ```sh
    docker compose up
    ```
6. **Export some environmental variables**:
    ```sh
    export BROKER_URL=localhost:9092
    ```

## Usage

After starting Kafka, you can proceed with running the Kafka demo project.


##  Useful CLI commands

1. **prerequisites**: Export some environmental variables
    ```sh
    export BROKER_URL=localhost:9092
    ```


2. **List topics**:
    ```sh
    kafka-topics.sh --list --bootstrap-server $BROKER_URL
    ```


3. **Consume messages from topic**:
    ```sh
    kafka-console-consumer.sh --topic $TOPIC --from-beginning --bootstrap-server $BROKER_URL
    ```
