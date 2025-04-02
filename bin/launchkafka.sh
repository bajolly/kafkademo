#!/bin/bash

zookeeper-server-start.sh ~/kafka_2.13-3.9.0/config/zookeeper.properties &

kafka-server-start.sh ~/kafka_2.13-3.9.0/config/server.properties &
