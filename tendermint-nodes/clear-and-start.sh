#!/bin/bash 
# sudo rm -rf ./tmdata

export TEND="docker run --rm \
--name tendermint-test \
-v $PWD/work-tmp/config-node-1:/tendermint \
tendermint/tendermint:0.15"

$TEND replay_console

sudo chown -Rf alcereo:alcereo tmdata