echo "Build Admin Staging..."
./mvnw -pl admin-app -am clean package -Dmaven.test.skip=true
export KOS_ADMIN_TAG=$(cat ./VERSION.txt)-staging
docker build -f "./Dockerfile.admin.staging" -t registry.gitlab.com/bitplay1/kos/kos-backend/admin-app:$KOS_ADMIN_TAG .
docker push registry.gitlab.com/bitplay1/kos/kos-backend/admin-app:$KOS_ADMIN_TAG
