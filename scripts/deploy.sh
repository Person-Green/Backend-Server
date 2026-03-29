#!/bin/bash
set -eo pipefail

IMAGE=$1
COMPOSE_FILE="docker-compose.prod.yml"
BLUE_PORT=8090
GREEN_PORT=8091

if [ -z "$IMAGE" ]; then
  echo "Usage: deploy.sh <image>"
  exit 1
fi

cd ~/app
export IMAGE

# 초기 배포 (아무 컨테이너도 없을 때)
if ! docker ps --format '{{.Names}}' | grep -q 'app-'; then
  echo ">>> 초기 배포: blue (포트 $BLUE_PORT)"
  docker compose -f $COMPOSE_FILE --profile blue up -d
  echo "ACTIVE_PORT=$BLUE_PORT"
  exit 0
fi

# 현재 활성 슬롯 확인
if docker ps --format '{{.Names}}' | grep -q 'app-green'; then
  OLD_SLOT=green
  NEW_SLOT=blue
  NEW_PORT=$BLUE_PORT
else
  OLD_SLOT=blue
  NEW_SLOT=green
  NEW_PORT=$GREEN_PORT
fi

echo ">>> 배포 시작: $OLD_SLOT → $NEW_SLOT (포트 $NEW_PORT)"

# 1. 새 슬롯 시작
docker compose -f $COMPOSE_FILE --profile $NEW_SLOT up -d $NEW_SLOT

# 2. 헬스체크 (Docker healthcheck 기반)
echo ">>> 헬스체크 대기..."
MAX_RETRY=30
for i in $(seq 1 $MAX_RETRY); do
  STATUS=$(docker inspect --format='{{.State.Health.Status}}' "app-$NEW_SLOT" 2>/dev/null || echo "starting")
  if [ "$STATUS" = "healthy" ]; then
    echo ">>> 헬스체크 통과"
    break
  fi
  if [ "$i" -eq "$MAX_RETRY" ]; then
    echo ">>> 헬스체크 실패 - 롤백"
    docker compose -f $COMPOSE_FILE --profile $NEW_SLOT stop $NEW_SLOT
    docker compose -f $COMPOSE_FILE --profile $NEW_SLOT rm -f $NEW_SLOT
    exit 1
  fi
  echo "  대기 중... ($i/$MAX_RETRY) [status: $STATUS]"
  sleep 2
done

# 3. Nginx 전환은 GitHub Actions에서 처리
echo "ACTIVE_PORT=$NEW_PORT"

# 4. 이전 컨테이너 정리
sleep 3
docker compose -f $COMPOSE_FILE --profile $OLD_SLOT stop $OLD_SLOT 2>/dev/null || true
docker compose -f $COMPOSE_FILE --profile $OLD_SLOT rm -f $OLD_SLOT 2>/dev/null || true

# 5. 미사용 이미지 정리
docker image prune -f

echo ">>> 배포 완료: $NEW_SLOT (포트 $NEW_PORT)"
