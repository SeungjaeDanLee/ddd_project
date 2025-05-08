#!/bin/bash

# 작업 디렉토리로 이동
cd ~

# 기존 애플리케이션 디렉토리 삭제
rm -rf foot-off-1-backend

# 새 디렉토리 생성
mkdir foot-off-1-backend

# tar 파일 압축 해제
tar -xzf deploy.tar.gz -C foot-off-1-backend

# 작업 디렉토리로 이동
cd foot-off-1-backend

# gradlew 실행 권한 부여
chmod +x ./gradlew

# 리소스 파일 복사
cp -r ~/resources/* src/main/resources/

# 기존 프로세스 종료
pid=$(pgrep -f foot-off-1-backend.jar || echo "")
if [ ! -z "$pid" ]; then
    echo "Stopping existing application..."
    kill -15 $pid
    sleep 5
fi

# 애플리케이션 빌드
echo "Building application..."
./gradlew build -x test

# 애플리케이션 시작
echo "Starting application..."
nohup java -jar build/libs/foot-off-1-backend.jar > app.log 2>&1 &

echo "Application started successfully" 