## Example app for capacitor-callkeep

### Setup

1. `npm install`
2. Set appId in capacitor.config.json

Android:

1. Set applicationId to same thing in android/app/build.gradle
2. Add `android/app/google-services.json` from firebase console. Details here: https://capacitorjs.com/docs/guides/push-notifications-firebase
3. `npx cap sync android`
4. check setup is okay: `npx cap doctor android`
5. `npx cap open android`
6. make sure you have developer mode & USB debugging on your android phone
7. Run app in android studio
   - console logs are in the `Run` tab at the bottom of android studio
8. Test a notification in firebase cloud messaging console using **FCM token** from logs
   - **note**: Android/Google uses FCM tokens and Apple/iOS uses APN tokens, because FCM tokens can't do VoIP on iOS right now.
9. In your .bashrc or .zshrc add this line:\
   `export GOOGLE_APPLICATION_CREDENTIALS=/absolute/path/to/myapp-firebase-adminsdk-somenumbers.json`\
   You'll need to download that from firebase console.

iOS:

TODO

### Android app doesn't work

Worth a try:

1. close android studio
2. `rm -rf android`
3. `npx cap add android`
4. Add below info to `android/app/src/main/AndroidManifest.xml`:

```xml
<!-- Inside <application> tag -->
<service android:name="com.dyadical.capcallkeep.VoiceConnectionService" android:label="Wazo" android:permission="android.permission.BIND_TELECOM_CONNECTION_SERVICE">
    <intent-filter>
        <action android:name="android.telecom.ConnectionService" />
    </intent-filter>
</service>

<!-- Inside top-level <manifest> tag -->

<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<!-- calling stuff: -->
<uses-permission android:name="android.permission.MANAGE_OWN_CALLS" />
<uses-permission android:name="android.permission.READ_CALL_LOG" />
<!-- Needed only if your calling app reads numbers from the `PHONE_STATE`
intent action. The maxSdkVersion attribute is needed only if your
calling app uses the getLine1Number() or getMsisdn() methods:
-->
<uses-permission android:name="android.permission.READ_PHONE_STATE" android:maxSdkVersion="29" />
<!--
Needed only if your calling app uses the getLine1Number() or
getMsisdn() methods:
-->
<uses-permission android:name="android.permissions.READ_PHONE_NUMBERS" />

```

### iOS app doesn't work

Worth a try:

1. close xcode
2. `rm -rf ios`
3. `npx cap add ios`
4. Add below info to TK:

TODO
