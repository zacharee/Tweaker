# About
SystemUI Tuner is an app for viewing and modifying hidden settings on Android devices.

Make sure to read the [Terms](app/src/main/assets/terms.md) for a full description and privacy policy.

# Changelog
Available in [CHANGELOG.md](CHANGELOG.md).

# Building
SystemUI Tuner has some unconventional configurations that require special steps to be able to build.

First, SystemUI Tuner makes use of hidden APIs in Android. To avoid reflection, a special SDK JAR is used to directly access these APIs.
To successfully build, you'll need to grab the Android 10 (API 29) JAR from [here](https://github.com/anggrayudi/android-hidden-api/), and follow the instructions to install it.

Next, there are some locally-referenced dependencies. These dependencies are also available remotely, but they need to be replaced. 
Check the app-level [build.gradle](app/build.gradle) and the [settings.gradle](settings.gradle) files for instructions.