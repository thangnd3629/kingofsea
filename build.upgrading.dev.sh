echo "Build Upgrading Consumer Dev..."
./mvnw -pl upgrading-consumer -am clean package "-Dmaven.test.skip=true"
export UPGRADING_TAG=$(cat ./VERSION.txt)-dev
docker build -f "./Dockerfile.upgrading.dev" -t registry.gitlab.com/bitplay1/kos/kos-backend/upgrading-consumer:$UPGRADING_TAG .
docker push registry.gitlab.com/bitplay1/kos/kos-backend/upgrading-consumer:$UPGRADING_TAG
