#! /bin/bash

SCRIPT_DIR=${BASH_SOURCE%/*}
. "$SCRIPT_DIR/version.sh"

if [ -e depot_tools ]; then
	pushd depot_tools > /dev/null
	git pull
	popd > /dev/null
else
	git clone https://chromium.googlesource.com/chromium/tools/depot_tools.git
fi

export PATH=$PATH:$PWD/depot_tools

if [ -e webrtc_checkout ]; then
	pushd webrtc_checkout/ > /dev/null
else
	mkdir webrtc_checkout
	pushd webrtc_checkout/ > /dev/null
	fetch --nohooks webrtc_ios
fi

pushd src > /dev/null
if [ "$WEBRTC_COMMIT" != "" ]; then
	git checkout $WEBRTC_COMMIT
fi
gclient sync

#for PATCH in ../../patch/*.patch; do 
#  patch -p1 < $PATCH
#done

export ARGS="is_debug=false rtc_include_tests=false rtc_build_examples=false rtc_build_tools=false use_custom_libcxx=false treat_warnings_as_errors=false"
gn gen out/osx-x86_64 -args="target_os=\"mac\" target_cpu=\"x64\" $ARGS"
ninja -C out/osx-x86_64

gn gen out/osx-arm64 -args="target_os=\"mac\" target_cpu=\"arm64\" $ARGS"
ninja -C out/osx-arm64

export ARGS="$ARGS ios_enable_code_signing=false"
gn gen out/ios-arm64 -args="target_os=\"ios\" target_cpu=\"arm64\" $ARGS"
ninja -C out/ios-arm64

# Simulator environemnts
gn gen out/iossim-x86_64 -args="target_os=\"ios\" target_cpu=\"x64\" $ARGS"
ninja -C out/iossim-x86_64

gn gen out/iossim-arm64 -args="target_os=\"ios\" target_environment=\"simulator\" target_cpu=\"arm64\" $ARGS"
ninja -C out/iossim-arm64


popd > /dev/null
popd > /dev/null

