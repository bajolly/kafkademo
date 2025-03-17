# Kafka Demo

This project demonstrates the usage of Apache Kafka. 

## Prerequisites

Make sure you have an instance of Kafka running. You can start Kafka using the following steps:

1. **Download Kafka**: If you haven't already, download Kafka from the [official website](https://kafka.apache.org/downloads).

2. **Install Kafka**: Assuming you unzip Kafka to ${HOME}/kafka_2.13-3.9.0 ( your version may vary ).  Add the `bin` directory to the `$PATH` environmental variable.

3. **Start Zookeeper**: Kafka requires Zookeeper to manage its cluster. Start Zookeeper with the following command:
    ```sh
    zookeeper-server-start.sh ~/kafka_2.13-3.9.0/config/zookeeper.properties
    ```

4. **Start Kafka**: Once Zookeeper is running, start Kafka with the following command:
    ```sh
    kafka-server-start.sh ~/kafka_2.13-3.9.0/config/server.properties
    ```
5. **Start Required Services** The Demo Project expects ChromaDB and Postgres to be running.  Start those services by running `docker compose up`


## Usage

After starting Kafka, you can proceed with running the Kafka demo project.
