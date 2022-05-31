#! /bin/bash

apt update && apt install -y \
        make \
        build-essential \
        pkg-config \
        python \
	zip \
	xcompmgr \
	x11-utils \
	uuid-dev \
	subversion \
	ruby \
	rpm \
	php7.4-cgi \
	p7zip \
	openbox \
	mesa-common-dev \
	libxtst6 \
	libxtst-dev \
	libxt-dev \
	libxss-dev \
	libxslt1-dev \
	libxshmfence-dev \
	libxrender1 \
	libxrandr2 \
	libxkbcommon-dev \
	libxinerama1 \
	libxi6 \
	libxfixes3 \
	libxdamage1 \
	libxcursor1 \
	libxcomposite1 \
	libx11-xcb1 \
	libwww-perl \
	libwayland-egl1-mesa \
	libvulkan1 \
	libvulkan-dev \
	libva-dev \
	libudev-dev \
	libssl-dev \
	libsqlite3-dev \
	libspeechd2 \
	libspeechd-dev \
	libsctp-dev \
	libpulse0 \
	libpulse-dev \
	libpng16-16 \
	libpixman-1-0 \
	libpci3 \
	libpci-dev \
	libpango-1.0-0 \
	libnss3-dev \
	libnss3 \
	libnspr4-dev \
	libnspr4 \
	libkrb5-dev \
	libjpeg-dev \
	libinput10 \
	libinput-dev \
	libgtk-3-dev \
	libgtk-3-0 \
	libglu1-mesa-dev \
	libglib2.0-dev \
	libgbm1 \
	libgbm-dev \
	libfreetype6 \
	libfontconfig1 \
	libffi-dev \
	libevdev2 \
	libevdev-dev \
	libelf-dev \
	libegl1 \
	libdrm2 \
	libdrm-dev \
	libcurl4-gnutls-dev \
	libcups2-dev \
	libcups2 \
	libcap2 \
	libcap-dev \
	libcairo2-dev \
	libcairo2 \
	libbz2-dev \
	libbrlapi0.7 \
	libbrlapi-dev \
	libbluetooth-dev \
	libatspi2.0-dev \
	libatspi2.0-0 \
	libatk1.0-0 \
	libasound2-dev \
	libapache2-mod-php7.4 \
	gperf \
	elfutils \
	devscripts \
	dbus-x11 \
	cdbs \
	binutils-mipsel-linux-gnu \
	binutils-mips64el-linux-gnuabi64 \
	binutils-arm-linux-gnueabihf \
	binutils-aarch64-linux-gnu \
	apache2-bin \
&& rm -rf /var/lib/apt/lists/*
