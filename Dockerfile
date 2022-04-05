FROM ubuntu:20.04
ARG DEBIAN_FRONTEND=noninteractive

COPY . /build/webrtc
WORKDIR /build/webrtc

RUN scripts/linux_packages.sh

CMD scripts/build_linux.sh && scripts/package.sh && cp webrtc*.zip /out


