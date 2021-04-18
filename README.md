# System UI Tuner

# Intro
SystemUI Tuner is a replacement and extension of AOSP's included System UI Tuner.

With SystemUI Tuner you can, among other things:
 - Hide status bar icons
 - Enable system-wide Immersive Mode and tweak its behavior per-app
 - Modify the behavior and appearance of the Quick Settings shade
 - Enable and customize Demo Mode
 - Change system-wide animation speeds
 - Enter and read your own custom settings values
 
# User Support
I make no guarantee with this app that:
 - it will work on your particular device
 - it will not not cause your device to malfunction
 - every feature will work on your device

SystemUI has no warranty. If you choose to use this app, I am not responsible for anything that goes wrong.
I will do my best to help you provided that:
 - I can
 - you have read this document and its warnings
 - you don't ask a question already answered here
 
# Permissions
SystemUI Tuner asks for some sensitive permissions:
 - android.permission.WRITE_SECURE_SETTINGS
 - android.permission.DUMP
 - android.permission.PACKAGE_USAGE_STATS
 
These permissions are not used to collect any sort of data, cause harm to your device (except by your own volition), or for any other malicious intent. SystemUI Tuner is open source (you are currently on the source page), and you are welcome to verify the validity of this claim.

Below I have explained the reason(s) each permission is needed:

WRITE_SECURE_SETTINGS
 - Without this, SystemUI Tuner just won't work. Hiding status bar icons, modifying Immersive Mode, changing Quick Settings, etc, all need this permission to function.

DUMP
 - This permission is needed for Demo Mode to function.
 - This permission is needed for the Status Bar icon auto-detect feature.
 
PACKAGE_USAGE_STATS
 - This permission is needed for the Status Bar icon auto-detect feature.
 
I have attempted to modularize when these permissions are requested. All three are requested during setup, but *only* WRITE_SECURE_SETTINGS is required at this point. If you skip the other permissions at this time, they will be requested when you attempt to use a function that requires them.

While each of these permissions requires a special process to grant them, they are no different from permissions such as CAMERA or MICROPHONE. That is, as soon as you uninstall SystemUI Tuner or clear its data, these permissions are revoked by Android.

# Uninstallation/Reset
SystemUI Tuner has a limited ability to restore settings to their defaults. It only works on Android Oreo (8.0) and later. 

Choosing the "Reset" option in the navigation drawer will prompt you with settings that can't be reset by SystemUI Tuner. Any custom settings you write may not be reset by SystemUI Tuner's reset function.

On Android Marshmallow and Nougat, you will need to manually reset all changes.

It is your responsibility to keep track of which settings you have changed, and reset them before you uninstall SystemUI Tuner. SystemUI Tuner *cannot* and *will not* automatically reset any settings when it is uninstalled.

# Persistent Options
Some options, such as the Icon Blacklist, will reset on reboot on certain devices. Other options may reset randomly. To prevent this, SystemUI Tuner has a "Persistent Options" menu, where options can be selected to become persistent. If any option selected in this menu changes, and the value does not match what SystemUI Tuner thinks it should be, SystemUI Tuner will set it back.

The Persistent Options menu can be accessed from the navigation drawer in the app. Tap the three-line (hamburger) menu in the top corner or swipe from the edge of the screen to open the drawer. Scroll down to find Persistent Options.
 
# Safety/Warnings
As mentioned in the introduction, I take no responsibility if you break your device. That said, I will lay out some warnings below, to supplement the many, _many_, **many** already present in the app:
 
TouchWiz (also known as Grace UX, Samsung Experience, and One UI) might break while using this app. See the TouchWiz section for details.

This app modifies system settings, and gives you the power to break your device. Use caution and only use this app if you are comfortable with manual recovery.

Immersive Mode is a **system** function. I did not make it and I do not control it. If problems arise through usage, *THIS IS THE FAULT OF YOUR MANUFACTURER*.

# Limitations
Since this app is **not** a system app, and does **not** have full system access, it has quite a few limitations.
 - This is **not** a theming app, nor is it meant to be! It cannot change the position, color or size of any system elements (with the exception of the status bar clock on Samsung Experience Oreo).
 - Some things simply will **not** function on your device! If certain status bar icon toggles have no effect, or Demo Mode does not function, there is absolutely nothing I can do.
 - SystemUI Tuner simply modifies system settings. It is not a "magic wand" that can do anything without root.

Explicit list of examples of functions SystemUI Tuner cannot do:
 - Move the clock (except on Samsung Experience Oreo)
 - Fix icon toggles that have no effect
 - Remove the carrier label (except on TouchWiz Oreo and above)
 - Add custom Quick Settings on Marshmallow devices that are more advanced than a simple broadcast
 - Change the color of status bar icons, Quick Settings, etc

Explicit list of examples of functions SystemUI Tuner cannot do on all devices:
 - Remove the dual-SIM icon
 - Remove the VoLTE icon
 - Modify the Quick Settings row and column counts
 - Modify the QS header count
 - Auto detect which status bar icons to hide
 - Hide any status bar icons
 - Enable Demo Mode
 - Change lockscreen shortcuts
 - Enable clock seconds
 - Enable in-battery percentage
 - Enable Night Mode/Display
 - etc
 
Neither list is exhaustive. The point is that SystemUI Tuner **CANNOT** do everything. If you email me asking (or demanding) that I "fix" something that does not work, I will simply ignore it.
 
# TouchWiz/Samsung Experience/One UI (Samsung)
TouchWiz is weird:
  - SystemUI Tuner **WILL NOT WORK** on TouchWiz Marshmallow.
  - Modifying status bar icons may cause crashes on TouchWiz Nougat.
  - The Rotation Lock icon may appear when modifying status bar icons. **READ THE WARNING IN THE APP TO LEARN HOW TO FIX THIS**
  
Since TouchWiz is so heavily modified from AOSP, this is simply how things are.
 
# MIUI (Xiaomi)
**DO NOT EXPECT THAT SYSTEMUI TUNER WILL WORK ON MIUI**
You may get lucky with a version where at least some functions work, but there is absolutely no guarantee, nor is there anything I can do about it.

# EMUI (Huawei)
EMUI Oreo and Pie have had decreasing compatibility with SystemUI Tuner. As with MIUI, this is out of my control.

# ColorOS (OPPO, Realme)
While SystemUI Tuner is more effective on ColorOS 7 than earlier versions, there are still quite a few changes made by OPPO that mean certain options simply don't work.


# Building
SystemUI Tuner has some unconventional configurations that require special steps to be able to build.

First, SystemUI Tuner makes use of hidden APIs in Android. To avoid reflection, a special SDK JAR is used to directly access these APIs.
To successfully build, you'll need to grab the Android 10 (API 29) JAR from [here](https://github.com/anggrayudi/android-hidden-api/), and follow the instructions to install it.

Next, there are some locally-referenced dependencies. These dependencies are also available remotely, but they need to be replaced. 
Check the app-level [build.gradle](app/build.gradle) and the [settings.gradle](settings.gradle) files for instructions.
