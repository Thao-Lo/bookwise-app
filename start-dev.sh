#!/bin/bash
echo "Starting Docker compose (Redis+Spring)..."
/usr/local/bin/docker compose -f docker-compose-dev.yml up -d --build
if [ $? -ne 0 ]; then
  echo " Failed to start Containers using Docker Compose"
  exit 1
fi
echo "Redis + Spring started"