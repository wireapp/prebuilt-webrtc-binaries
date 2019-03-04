Prebuilt WebRTC Library for Wire
================================

This repository holds prebuilt WebRTC libraries used in Wire applications

Building Instructions
---------------------

Clone this repo, run the build script for the platform you are on.

You must build on OSX for iOS and OSX and on Linux for Linux and Android.

```bash
$ ./scripts/build_iosx.sh
```

```bash
$ ./scripts/build_linux.sh
```


The scripts are implementations of the instructions given at https://webrtc.org/native-code/development/ 

The script also applies patches found in the `patch` directory:

* webrct_android.patch - applies change to the JNI code needed when mixing JNI and JNA.

Once building is complete, run the package script to make zip files for each platform.

```bash
$ ./scripts/package.sh
```

Once run you will have zip files containing libraries for the relevant platforms and one for the headers. You can manually place them in your AVS checkout under `contrib/webrtc/<version>` and update the `WEBRTC_VER` variable in `mk/target.mk` to point to your version.

