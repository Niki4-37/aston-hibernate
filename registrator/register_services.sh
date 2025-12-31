#!/bin/sh

# waiting Kong
echo "Waiting for Kong to be ready..."
while ! curl -s http://kong-gateway:8001/ | grep -q "version"; do
  sleep 2
done
echo "Kong is ready!"

# Aston-service registration
echo "Registering service 'my-service'..."

curl -X POST http://kong-gateway:8001/services \
  --data name=aston-service \
  --data url=http://aston-app:8080 \
  -s -o /dev/null


if [ $? -eq 0 ]; then
  echo "Service 'aston-service' registered successfully."
else
  echo "Failed to register service 'my-service'."
  exit 1
fi

# Aston-service route registration
echo "Registering route for 'my-service'..."

curl -X POST http://kong-gateway:8001/services/aston-service/routes \
  --data paths[]=/api/aston-service \
  --data name=aston-service-route \
  -s -o /dev/null


if [ $? -eq 0 ]; then
  echo "Route for 'my-service' registered successfully."
else
  echo "Failed to register route for 'my-service'."
  exit 1
fi

echo "Service registration completed."
