### migratorydata-demo-chat-fcm-client-android

Building a Real-time Chat App with Android, MigratoryData, and Firebase Cloud Messaging (FCM)

Welcome to the documentation on building a real-time chat application using Android, MigratoryData for real-time messaging, and Firebase Cloud Messaging (FCM) for notifications. In this guide, we'll walk you through the process of setting up Android project, integrating Firebase Messaging, and utilizing MigratoryData Dart API for seamless real-time communication.

### Prerequisites

Before we begin, ensure you have the following prerequisites installed and set up:

- MigratoryData Server 6.0.16 or later with Presence extension. see repo [migratorydata-demo-chat-fcm-plugin](https://github.com/migratorydata/migratorydata-demo-chat-fcm-plugin) for more details.
- Android Studio: Install Android Studio by following the official installation guide for your platform: [Android Studio Installation Guide](https://developer.android.com/codelabs/basic-android-kotlin-compose-install-android-studio#0)
- Firebase Account: Set up a new project in the [Firebase Console](https://console.firebase.google.com/) or use the existing project associated with the [fcm-plugin](https://github.com/migratorydata/migratorydata-demo-chat-fcm-plugin).

### Getting Started

1. Install [fcm-plugin]((https://github.com/migratorydata/migratorydata-demo-chat-fcm-plugin)) and run the Migratorydata server with presence extension. See the [migratorydata-demo-chat-fcm-plugin](https://github.com/migratorydata/migratorydata-demo-chat-fcm-plugin) for more details.

2. Clone this repository to your local machine.

3. Configuring Firebase Messaging.

The project has been pre-configured with all the essential libraries required for Firebase messaging functionality. The only missing component is the configuration file named `google-services.json`, which can be obtained from the Firebase dashboard under project settings. Once downloaded, add the `google-services.json` file to the `app` directory of your project.

For further guidance on configuring an Android project with Firebase, you can refer to the documentation provided at this [link](https://firebase.google.com/docs/android/setup).

4. Configure MigratoryData server address in file `ChatApp.java`.
5. Launch the application on emulator or device.

### Android API documentation

For further details, please refer the documentation at:

https://migratorydata.com/docs/client-api/android/getting_started/