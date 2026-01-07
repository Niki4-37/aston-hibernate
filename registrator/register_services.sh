#!/bin/sh

# ------------------------------------------------------------------
# 1. Wait until Kong is ready (with timeout)
# ------------------------------------------------------------------
echo "Waiting for Kong to be ready (max 60 seconds)..."
timeout=60
while [ $timeout -gt 0 ]; do
    if curl -s http://kong-gateway:8001/ | grep -q "version"; then
        echo "Kong is ready!"
        break
    fi
    sleep 2
    timeout=$((timeout - 2))
done

if [ $timeout -le 0 ]; then
    echo "Error: Kong did not become ready within 60 seconds."
    exit 1
fi

# ------------------------------------------------------------------
# 2. Helper functions
# ------------------------------------------------------------------
check_service_exists() {
    local svc_name=$1
    curl -s -o /dev/null -w "%{http_code}" http://kong-gateway:8001/services/$svc_name | grep -q "200"
}

register_service() {
    local svc_name=$1
    local svc_url=$2

    if check_service_exists "$svc_name"; then
        echo "Service '$svc_name' already exists. Skipping registration."
        return
    fi

    echo "Registering service '$svc_name'..."

    curl -X POST http://kong-gateway:8001/services \
         --data name="$svc_name" \
         --data url="$svc_url" \
         -s -o /dev/null

    if [ $? -eq 0 ]; then
        echo "Service '$svc_name' registered successfully."
    else
        echo "Failed to register service '$svc_name'."
        exit 1
    fi
}

register_route() {
    local svc_name=$1
    local route_path=$2

    echo "Registering route for '$svc_name'..."

    curl -X POST http://kong-gateway:8001/services/$svc_name/routes \
         --data paths[]="$route_path" \
         --data name="${svc_name}-route" \
         -s -o /dev/null

    if [ $? -eq 0 ]; then
        echo "Route for '$svc_name' registered successfully."
    else
        echo "Failed to register route for '$svc_name'."
        exit 1
    fi
}

# ------------------------------------------------------------------
# 3. Register services
# ------------------------------------------------------------------
register_service user-service http://user-service:8080
register_route   user-service /api
register_service notification-service http://notification-service:8082
register_route   notification-service /api/notification

echo "All services registration completed."

