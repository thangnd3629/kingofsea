echo "Build KOS Dev..."
./mvnw -pl kos-app -am clean package -Dmaven.test.skip=true
echo "Docker package"
export KOS_TAG=$(cat ./VERSION.txt)-dev
docker build -f "./Dockerfile.kos.dev" -t registry.gitlab.com/bitplay1/kos/kos-backend/kos-app:$KOS_TAG .
echo "Push image..."
docker push registry.gitlab.com/bitplay1/kos/kos-backend/kos-app:$KOS_TAG
