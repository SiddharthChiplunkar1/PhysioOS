#!/bin/bash
set -e

echo "Adding Bitnami Helm Repository for Database, Redis, and Kafka..."
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

echo "Installing PostgreSQL..."
helm upgrade --install physioos-postgres bitnami/postgresql \
  --set auth.postgresPassword=password \
  --set auth.database=physioos_db

echo "Installing Redis..."
helm upgrade --install physioos-redis bitnami/redis \
  --set auth.enabled=false \
  --set architecture=standalone

echo "Installing Kafka..."
helm upgrade --install physioos-kafka bitnami/kafka \
  --set zookeeper.enabled=false \
  --set kraft.enabled=true \
  --set replicaCount=1

echo "Adding Grafana Repository for Observability Stack..."
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update

echo "Installing Prometheus..."
helm upgrade --install physioos-prometheus bitnami/prometheus \
  --set server.replicaCount=1

echo "Installing Grafana..."
helm upgrade --install physioos-grafana grafana/grafana \
  --set adminPassword=admin

echo "Installing Loki..."
helm upgrade --install physioos-loki grafana/loki \
  --set loki.auth_enabled=false

echo "Installing Tempo..."
helm upgrade --install physioos-tempo grafana/tempo

echo "Infrastructure deployment triggered successfully!"
