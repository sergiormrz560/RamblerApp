# Ramble Android Mobile Application

### WebView

The Rambler Android application opens up the live www.transyrambler.com website in a WebView. Thanks to the WebView being an internet browser, the interface is automatically formatted for the mobile device. The WebView allows for the official website host to continue being the only thing managing the content, handling user authentication, and controlling the comment system.

### Push Notifications

Upon first loading the application, the device registers itself with both the [Firebase console](https://firebase.google.com/) and the [OneSignal dashboard](https://onesignal.com/). Both of these services allow for the manual pushing of notifications from the their interface. Within the wp-admin dashboard for the WordPress site, the OneSignal plugin was installed and configured by connecting it to the OneSignal account. Upon an article being posted, the plugin alerts the OneSignal servers which then tells Firebase to send the notification to the appropriate devices.

### How To Emulate

1) Install Android Studio
2) Create a new project and emulate it on a virtual device or a phone
3) Open a new project from source control using this GitHub url https://github.com/sergiormrz560/RamblerApp
4) Build the Gradle file
5) Run the Application