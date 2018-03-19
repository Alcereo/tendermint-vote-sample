#!/bin/bash 

sudo rm -rf ./work-tmp/config-node-1
mkdir ./work-tmp/config-node-1;
cp -R ./config-node-1 ./work-tmp/

sudo rm -rf ./work-tmp/config-node-2
mkdir ./work-tmp/config-node-2;
cp -R ./config-node-2 ./work-tmp/

sudo rm -rf ./work-tmp/config-node-3
mkdir ./work-tmp/config-node-3
cp -R ./config-node-3 ./work-tmp/

docker-compose up;
docker-compose down;

docker volume rm $(docker volume ls -q | grep tendermint);