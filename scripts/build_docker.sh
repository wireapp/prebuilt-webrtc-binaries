#! /bin/bash

docker build . -t webrtc
docker run -v $PWD:/out webrtc

