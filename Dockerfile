FROM ubuntu:20.04
ARG DEBIAN_FRONTEND=noninteractive

COPY . /build/webrtc
WORKDIR /build/webrtc

RUN /build/webrtc/scripts/ubuntu_20.04_dependencies.sh

