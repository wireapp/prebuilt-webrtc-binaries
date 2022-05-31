#! /bin/bash

docker build . -t webrtc
docker run -v $PWD:/out -t webrtc /bin/bash -c "scripts/build_linux.sh; scripts/package.sh; cp *.zip /out"
