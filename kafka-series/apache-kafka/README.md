# 1.搭建环境
Kafka 3.x 可以不使用 Zookeeper 搭建
```shell
docker pull apache/kafka:3.7.1
docker run -p 9092:9092 -d apache/kafka:3.7.1
```