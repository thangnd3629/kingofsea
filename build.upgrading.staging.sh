echo "Build Upgrading Consumer Staging..."
./mvnw -pl upgrading-consumer -am clean package "-Dmaven.test.skip=true"
export UPGRADING_TAG=$(cat ./VERSION.txt)-staging
docker build -f "./Dockerfile.upgrading.staging" -t registry.gitlab.com/bitplay1/kos/kos-backend/upgrading-consumer:$UPGRADING_TAG .
docker push registry.gitlab.com/bitplay1/kos/kos-backend/upgrading-consumer:$UPGRADING_TAG
