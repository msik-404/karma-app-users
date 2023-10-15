#! /usr/bin/bash

# stop containers
DIR=$(dirname $BASH_SOURCE)
source "$DIR/stop.sh"

# delete containers
docker rm karma-app-users-backend-1 && docker rm karma-app-users-mongo-express-1 && docker rm karma-app-users-mongo-1

# deleted backend image
docker image rm karma-app-users-backend

# delete volumes
docker volume rm karma-app-users_db-config && docker volume rm karma-app-users_db-data