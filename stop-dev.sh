#!/bin/bash
echo "Stop Redis..."
/usr/local/bin/docker compose -f docker-compose-dev.yml down 
echo "Redis is stopped"