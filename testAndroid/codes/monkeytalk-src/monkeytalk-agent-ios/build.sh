#!/bin/bash
VERSION=${1-unknown}
CONFIG=${2-Debug}
SDK=${3-8.1}

function generateMTWebJs {
  js=$(tr "\n" "\\" < ./MonkeyTalk/monkeytalk-web.js)
  js=$(echo $js | sed -e "s|\\\|\\\\\n|g")
  out=$(echo $js | sed -e "s|\"|\\\\\"|g")
  echo '#define MTWebJsString @"' $out '"' > ./MonkeyTalk/MTWebJs.h
  echo "Generated MTWebJs.h"
}

function build {
  rm -rf build/$CONFIG-*
  xcodebuild -target $1 -configuration $CONFIG -sdk iphonesimulator$SDK build
  xcodebuild -target $1 -configuration $CONFIG -sdk iphoneos$SDK build
  lipo -create build/Debug-iphoneos/lib$1.a build/Debug-iphonesimulator/lib$1.a -o build/lib$1-$VERSION.a
  echo "Built library: lib$1-$VERSION.a"
}

echo build.sh invoked with parameters 1=$1 2=$2 3=$3 
echo build.sh translated to VERSION=$VERSION CONFIG=$CONFIG SDK=$SDK 
rm -rf build
generateMTWebJs
build MonkeyTalk
build MonkeyTalkMediaPlayer

