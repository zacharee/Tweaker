# 353
- Fix an issue with SeekBars not working.
- Fix issues with light mode.
- Improve UX in Night Mode option.
- Crash fixes.

# 352
- Fix some issues with revertable settings.
- Fix dialog behavior for small displays.
- Fix icon blacklist persistence.
- Update dependencies.

# 351
- Fix showing failure screen for writing to Settings.System.
- Make sure UI states properly revert when settings are reverted.
- Address an ANR when checking for root access.
- Add some info to the UI Sounds option about needing to restart System UI to load the sounds
- Fix intro slider color fade.
- Hide Shizuku tutorial in intro slider below Android 11.
- Add Turkish translation.
- Update translations.

# 350
- Remove a lot of Google dependencies.
- Migrate to Bugsnag from Crashlytics.
- Redo intro sliders in Compose.
- Add a toggle to disable/enable crash reports.
- Fix search icon color.
- Fix disappearing notices in Persistent Options.
- Update some text in the tutorial and intro slides.
- The intro will appear once even if you've already gone through it in this update. Just exit it and it shouldn't show up again.
- Add Mastodon link.
- Use Material Design 3 switches.

# 349
- Update translations.
- Add Italian translation.
- Crash fixes.
- Remove OneUI Tuner references.
- Update icon colors for Android 12+.
- Update dependencies.
- Show keys for predefined Icon Blacklist items.

# 348
- Crash fixes.
- Update translations.
- After editing a custom persistent option, the new value will be immediately written to the device settings.
- Hide OneUI Tuner link on One UI 4 and later.
- Add option to hide lock screen ads on FireOS.
- Add option to allow custom left lock screen shortcut on FireOS.
- Move lock screen settings to specific Lock Screen section.
- Fix lock screen shortcuts resetting on One UI.
- Add "Flashlight", "Do Not Disturb", and "None" options for One UI lock screen shortcuts.

# 347
- Try to prevent reports of OBSERVE_GRANT_REVOKE_PERMISSIONS crashes.
- Make sure AD_ID permission isn't in manifest.
- Address deprecations.

# 346
- Fix temperature slider in Night Mode option.
- Implement a "revert" dialog for dangerous settings.
- UI tweaks.
- Update dependencies.
- Update Immersive Mode description to mention broken Immersive Mode on Android 11 and later.
- Update intro to use Material You theming.
- Request notification permission on Android 13.
- Target API 33.

# 345
- Dangerous preferences show up in red again.
- Add a warning about Night Mode to the terms.
- Implement Material You theming for Android 12 and later.
- Update dependencies.

## 344
- Add Hungarian translation.
- Update translations.

## 343
- Code cleanup.
- Update dependencies.
- Crash fixes.

## 342
- Fix some crashes.
- Update translations.
- Add in a prompt to enable SYSTEM_ALERT_WINDOW on Android 12 for background operations.

## 341
- Disable the lock screen shortcuts option if it's unavailable on the current device.
- Try a workaround for foreground Service crashes.
- Update translations.

## 340
- Crash fixes.
- Target API 32.
- Code cleanup.

## 339
- Crash fixes.
- Update dependencies.
- Remove local dependencies for easier builds.
- Include OnePlus in the instructions for OPPO/Realme.
- Update some text.
- Make permissions pages show clearer status.
- Add Czech translation.
- Update translations.
- Add Spanish translation.

## 338
- Update dependencies.
- Add Traditional Chinese translation.
- Crash fixes.

## 337
- Code cleanup.
- Move the search function to the new drawer.

## 336
- Update dependencies.
- Make the Traditional Chinese translations show up for rTW instead of rCN.

## 335
- Add some missing content descriptions.
- Crash fixes.
- Explicitly show that the non-resettable keys in the reset dialog are from Settings.System.
- UI tweaks.
- Update dependencies.
- Update translations.
- Fix an issue where immersive mode wasn't activating properly.

## 334
- Fix the QS editor on One UI.
- Work on making the QS editor look and work better.
- Code tweaks.
- Migrate away from old drawer layout and put the previous home screen as the drawer.
- Tweak preference layout.
- Add some animations.
- Crash fixes.

## 333
- Crash fixes.

## 332
- Update terms to include more permission explanations.
- Use Shizuku for writing to Settings.System if possible.
- Fix an issue with the lock screen shortcut selector where items would show when they weren't supposed to.
- Update dependencies.
- Add option to split "Internet" tile back up into "WiFi" and "Cell" tiles on Android 12.
- Add some missing QS icons to the editor.
- Prioritize title match over summary match when searching for preferences.
- Update translations.
- Crash fixes.
- Update how SlidingPaneLayout is used to address changes to its UI.
- Code cleanup.
- Work on better tablet layouts.

## 331
- Crash fixes.
- Code cleanup.
- Update translations.
- Add option to hide multi-SIM panel on One UI.
- Disable QS header count option after Android 11.
- Add QUERY_ALL_PACKAGES permission to properly list all apps in lock screen shortcut selection and immersive mode.
- Sort apps case-insensitively in the immersive mode selector.
- Add fast scroll to immersive mode selector and lock screen shortcut selector.
- Update lock screen shortcuts icon to with with One UI.
- Add a workaround for restrictions on reading settings in Android 12.

## 330
- Fix filename format for icon blacklist backup.
- Migrate from libsuperuser to libSU.
- Update target SDK to 31.
- Replace old hidden API blacklist with one from LSposed.
- Add option to specify custom QS tile keys in QS editor.
- Update dependencies.
- Replace hidden API usage with public API.
- Make the "Disable Safe Audio Warning" option more flexible.
- Crash fixes.
- Add hint to Persistent Options screen to disable battery optimization.

## 329
- Mark UI sounds preference as dangerous.
- Update dependencies.
- Make some more strings translatable.
- Include other strings files in Crowdin config.
- Remove unused strings.
- Add more options to Demo Mode.
- Update Night Mode description.
- Add screen to manage SystemUI Tuner's QS tiles.
- Disable Night Mode QS tile by default.

## 328
- Update dependencies.
- Crash fixes.
- Code cleanup.
- Update terms.
- Update translations for Portuguese, Russian, Traditional Chinese.
- Add Korean translation.
- Add Crowdin link.

## 327
- Update terms.
- Update dependencies.
- Code cleanup.
- More ViewBinding fixes.

## 326
- Code cleanup.
- Crash fixes
- Update dependencies.
- Update QS Header count description to say it's incompatible with One UI.
- Update OneUI Tuner link.
- Update Settings.System add-on link.

## 325
- Update dependencies.
- ViewBinding crash fixes.

## 324
- Fix a crash in the custom persistent options dialog caused by ViewBinding.

## 323
- Fix a Shizuku issues.
- Move away from Kotlin synthetics to ViewBinding.

## 322
- Fix a Russian translation issue.
- Add a Patreon supporters dialog and Patreon link.
- UI tweaks for bottom sheets.
- Update Shizuku implementation with new library and error handling.
- Code tweaks.

## 321
- You can now use Shizuku for granting permissions.
- UI tweak for option summary expansion.
- Add Portuguese, Traditional Chinese, and Russian translations.