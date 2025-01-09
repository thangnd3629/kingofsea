echo "Build Crob Job Staging..."
./mvnw -pl cron-job -am clean package -Dmaven.test.skip=true
echo "Docker package"
export KOS_TAG=$(cat ./VERSION.txt)-staging
docker build -f "./Dockerfile.cronjob.staging" -t registry.gitlab.com/bitplay1/kos/kos-backend/cron-job:$KOS_TAG .
echo "Push image..."
docker push registry.gitlab.com/bitplay1/kos/kos-backend/cron-job:$KOS_TAG
