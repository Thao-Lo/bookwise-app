#!/bin/bash
echo "Starting Redis..."
/usr/local/bin/docker compose -f docker-compose-dev.yml up -d redis
if [ $? -ne 0 ]; then
  echo " Failed to start Redis using Docker Compose"
  exit 1
fi
echo "Redis started"