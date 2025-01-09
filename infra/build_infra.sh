#! /bin/bash
source .env


sudo mkdir -p ~/.kos/mount/staging/postgres
sudo mkdir -p ~/.kos/mount/staging/redis
sudo mkdir -p ~/.kos/mount/staging/rabbitmq
sudo mkdir -p ~/.kos/mount/staging/zookeeper
sudo mkdir -p ~/.kos/mount/staging/kafka
sudo chown 1001 ~/.kos/mount/staging/zookeeper
sudo chown 1001 ~/.kos/mount/staging/kafka
docker-compose up -d