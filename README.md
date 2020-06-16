# Customize InApp in your app with Acoustic Android SDK 

This sample app demonstrates how to customize Acoustic InApp plugin using Acoustic Android SDK

### General

- In this app, `plugininapp` is a Java module. The steps to create this library:
- Switch to the project perspective in Android Studio 
- Right click on your project and select new module
- Select Android Library
-- Name the module for example **plugin-inapp** 
-- Set the package of the module to co.acoustic.mobile.push.sdk.plugin.inapp
-- Click Finish
- Unzip acoustic-mobile-push-android-sdk-plugin-inapp-3.8.1.zip file located at the plugin folder of the SDK package.
- From src/main folder - copy the assets, java and res folders into the app new module (plugin-inapp) 
- In the app's module build.gradle file add in the dependencies section:
- implementation project(':plugin-inapp')
       	

### To Download Acoustic Android SDK, plugin and Sample app 
 To download Acoustic Android SDK and plugins use [Acoustic Android 3.8.1 SDK](https://github.com/Acoustic-Mobile-Push/android)

