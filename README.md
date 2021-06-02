# Sharingang
[![Build Status](https://api.cirrus-ci.com/github/Sharingang/Sharingang-Android.svg)](https://cirrus-ci.com/github/Sharingang/Sharingang-Android)
[![Maintainability](https://api.codeclimate.com/v1/badges/774b529741401b3b13f1/maintainability)](https://codeclimate.com/github/Sharingang/Sharingang-Android/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/774b529741401b3b13f1/test_coverage)](https://codeclimate.com/github/Sharingang/Sharingang-Android/test_coverage)

## Project setup
- Download the `google-services.json` file from the [Firebase console](https://console.firebase.google.com/u/0/project/sharingang-app/settings/general/android:com.example.sharingang) and place it under in the `app/` folder
- Install [Firebase CLI](https://firebase.google.com/docs/cli?hl=en)
- Copy the `.runtimeconfig.json` file in the `functions` directory
- Run `npm install` in the `functions` directory
- Launch the Firebase emulator `firebase emulators:start`

### Release build
When building the app in release mode, it will automatically use the real Firebase servers instead of the emulator. Your signing key needs to be added on the [Firebase console](https://console.firebase.google.com/u/0/project/sharingang-app/settings/general/android:com.example.sharingang).

### Using Firebase emulator on a physical device
If you want to run the app on a physical phone instead of the Android emulator, you will have to replace the IP address in `RepositoryModule` by the IP address of your computer.
