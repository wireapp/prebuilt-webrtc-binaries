#! /bin/bash

SCRIPT_DIR=${BASH_SOURCE%/*}
. "$SCRIPT_DIR/version.sh"

if [ -e depot_tools ]; then
	pushd depot_tools >/dev/null
	git pull
	popd
else
	git clone https://chromium.googlesource.com/chromium/tools/depot_tools.git
fi

export PATH=$PATH:$PWD/depot_tools

if [ -e webrtc_checkout ]; then
	pushd webrtc_checkout/ >/dev/null
else
	mkdir webrtc_checkout
	pushd webrtc_checkout/ >/dev/null
	fetch --nohooks webrtc_android
fi

pushd src > /dev/null

git checkout $WEBRTC_COMMIT

yes | gclient sync

popd >/dev/null
popd >/dev/null

# Glient sync updates depot_tools to latest version.
# Use depot tools with ninja binary
# since newer depot-tools use Ninja binary from cipd DEPS
pushd depot_tools >/dev/null
git checkout 1f67d5573f9cc19bc7fd52b0295687164cc979d6
popd >/dev/null

pushd webrtc_checkout >/dev/null
pushd src >/dev/null

for PATCH in ../../patch/*.patch; do 
  patch -p1 < $PATCH
done

. build/android/envsetup.sh 

export ARGS="is_debug=false rtc_include_tests=false rtc_build_examples=false rtc_build_tools=false use_custom_libcxx=false treat_warnings_as_errors=false"
gn gen out/linux-x86_64 -args="target_os=\"linux\" target_cpu=\"x64\" $ARGS"
ninja -C out/linux-x86_64

gn gen out/android-i386 -args="target_os=\"android\" target_cpu=\"x86\" $ARGS"
ninja -C out/android-i386

gn gen out/android-x86_64 -args="target_os=\"android\" target_cpu=\"x64\" $ARGS"
ninja -C out/android-x86_64

gn gen out/android-armv7 -args="target_os=\"android\" target_cpu=\"arm\" $ARGS"
ninja -C out/android-armv7

gn gen out/android-arm64 -args="target_os=\"android\" target_cpu=\"arm64\" $ARGS"
ninja -C out/android-arm64

popd > /dev/null
popd > /dev/null

