# About
SystemUI Tuner is an app for viewing and modifying hidden settings on Android devices.

Make sure to read the [Terms](app/src/main/assets/terms.md) for a full description and privacy policy.

# Changelog
Available in [CHANGELOG.md](CHANGELOG.md).

# Building
SystemUI Tuner makes use of hidden APIs in Android. To avoid reflection, a special SDK JAR is used to directly access these APIs.
To successfully build, you'll need to grab the Android 12 (API 31) JAR from [here](https://github.com/Reginer/aosp-android-jar), and follow the instructions to install it.