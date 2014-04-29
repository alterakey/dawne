#/bin/bash
intent=$1
pkg=$2
activity=$3

intr() {
  adb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME
  exit 0
}

trap intr INT

adb shell am start -a $intent -n $pkg/$activity &
adb logcat
