#!/bin/sh

# Health check script for Nginx
# Returns 0 if healthy, non-zero otherwise

# Check if Nginx is running
if ! nc -z localhost 80 2>/dev/null; then
    exit 1
fi

# Try to get a response from the health endpoint
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/health 2>/dev/null)

if [ "$response" = "200" ]; then
    exit 0
else
    exit 1
fi

