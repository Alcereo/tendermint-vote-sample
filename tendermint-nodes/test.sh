#!/bin/bash 

docker run --rm \
--net host \
--name tendermint-test \
-v $PWD/tmdata:/tendermint \
tendermint/tendermint \
$@
