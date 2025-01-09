echo "Build Admin Dev..."
./mvnw -pl admin-app -am clean package -Dmaven.test.skip=true
export KOS_ADMIN_TAG=$(cat ./VERSION.txt)-dev
docker build -f "./Dockerfile.admin.dev" -t registry.gitlab.com/bitplay1/kos/kos-backend/admin-app:$KOS_ADMIN_TAG .
docker push registry.gitlab.com/bitplay1/kos/kos-backend/admin-app:$KOS_ADMIN_TAG
