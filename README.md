# Sharingang
[![Build Status](https://api.cirrus-ci.com/github/Sharingang/Sharingang-Android.svg)](https://cirrus-ci.com/github/Sharingang/Sharingang-Android)

## Using Firebase
- Download the `google-services.json` file from the [Firebase console](https://console.firebase.google.com/u/0/project/sharingang-app/settings/general/android:com.example.sharingang) and place it under in the `app/` folder
- Install [Firebase CLI](https://firebase.google.com/docs/cli?hl=en)

When running the app, you will have to run the Firebase emulator. For that, go to the root directory of the project and run:
`firebase emulators:start --only firestore`

If you want to run the app on a physical phone instead of the emulator, you will have to replace the IP address in `FirestoreItemRepository` by the IP address of your computer.