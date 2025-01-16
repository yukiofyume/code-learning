**# code-learning**
学习不同的博客和教程
# 1.基础环境
基于`zulu-Jdk21`、 `SpringBoot 3.2.7` 和 `SpringCloud 2023.0.3`
## **1.1.Nacos**

```bash
git clone https://github.com/nacos-group/nacos-docker.git
cd nacos-docker
```

之前有下过部分镜像，最近🪜不给力，在 `issues` 上抄了个作业
`/etc/docker/standalone-mysql-8.yaml`

```bash
version: "3.0"
services:
# MYSQL
  mysql:
    container_name: mysql
    image: mysql:8.0.29
    volumes:
      - ./base/mysql/data:/var/lib/mysql
      - ./base/mysql/conf/my.cnf:/etc/mysql/my.cnf
      - ./base/mysql/mysql-files:/var/lib/mysql-files
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=root
      - MYSQL_DATABASE=nacos
      - MYSQL_USER=nacos
      - MYSQL_PASSWORD=nacos
      - LANG=C.UTF-8
      - MYSQL_ROOT_HOST=%
    privileged: true
    restart: always
    healthcheck:
      test: [ "CMD", "mysqladmin" ,"ping", "-h", "localhost" ]
      interval: 5s
      timeout: 10s
      retries: 10
# Nacos
  nacos:
    image: nacos/nacos-server:v2.3.1-slim
    container_name: nacos-standalone-mysql
    environment:
      - TZ=Asia/Shanghai
      - PREFER_HOST_MODE=hostname
      - MODE=standalone
      - MYSQL_SERVICE_HOST=mysql
      - MYSQL_SERVICE_DB_NAME=nacos
      - MYSQL_SERVICE_PORT=3306
      - MYSQL_SERVICE_USER=nacos
      - MYSQL_SERVICE_PASSWORD=nacos
      - MYSQL_SERVICE_DB_PARAM=characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    volumes:
      - ./base/nacos/logs/:/home/nacos/logs
    ports:
      - "8848:8848"
      - "9848:9848"
    restart: always
    depends_on:
      mysql:
        condition: service_healthy
```

执行命令

```bash
docker-compose -f standalone-mysql-8.yaml up
```

后续用 dbeaver 或者 navicat 无法连接数据库报错 `Public Key Retrieval is not allowed` 的时候，修改下数据库连接属性就好了，`AllowPublicKeyRetrieval=true`