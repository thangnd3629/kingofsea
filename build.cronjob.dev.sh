echo "Build Crob Job Dev..."
./mvnw -pl cron-job -am clean package -Dmaven.test.skip=true
echo "Docker package"
export CRON_JOB_TAG=$(cat ./VERSION.txt)-dev
docker build -f "./Dockerfile.cronjob.dev" -t registry.gitlab.com/bitplay1/kos/kos-backend/cron-job:$CRON_JOB_TAG .
echo "Push image..."
docker push registry.gitlab.com/bitplay1/kos/kos-backend/cron-job:$CRON_JOB_TAG
