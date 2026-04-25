#!/bin/bash

# Setup script for AGE database initialization
# Run this script after starting your AGE PostgreSQL container

echo "Setting up AGE database..."

# Connect to PostgreSQL and initialize AGE
docker exec -it myPostgresDb psql -U postgresUser -d postgresDB <<EOF
CREATE EXTENSION IF NOT EXISTS age;
LOAD 'age';
SET search_path = ag_catalog, "\$user", public;
SELECT create_graph('demo_graph');
EOF

echo "Database setup completed!"