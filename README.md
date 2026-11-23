# Screen Timer

A tiny Android app that shows today's screen time as an always-on floating timer.

- Counts only while the screen is on **and** the phone is unlocked
- Resets at midnight, keeps a 7-day history
- Drag the bubble anywhere; tap it to open the app
- Restarts itself after a reboot
- 100% local: no internet permission, no accounts

## Get the APK (no Android Studio needed)

1. Create a new repo on GitHub and upload this whole folder (keep the `.github` folder).
2. Open the repo's **Actions** tab and wait for **Build APK** to finish (~3–5 min).
3. Open that run, download **ScreenTimer-apk**, and unzip it to get `app-debug.apk`.
4. Send it to your phone, open it, and allow "Install unknown apps" when asked.

## Or with Android Studio

Open this folder, press ▶ Run with your phone plugged in (USB debugging on).

## First launch

1. Tap **Allow overlay permission** and switch it on for Screen Timer.
2. Tap **Turn overlay ON**.
3. Recommended: Settings → Apps → Screen Timer → Battery → **Unrestricted**,
   so the phone doesn't kill it in the background (Samsung/Xiaomi/Oppo are aggressive about this).
