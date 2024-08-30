# FollowMe SDK Example

This project is meant to help anyone who wants to use our [MapTrip SDK](https://www.maptrip.de/sdk-lkw-navigation) to create their own FollowMe Application.  
It consists of an Android Studio Project with commented code, in which you can see how we handle the SDK-functions and -callbacks.

## Setup

To use this App, you have to make a few preparations first.
Contact the [infoware support](https://www.maptrip.de/kontakt/) to get the [MapTrip SDK package](https://www.maptrip.de/docs/sdk/get.html).

From the SDK package, just copy the files *data.zip* and *res.zip* from another example app, e.g. *MapTripSDK\example\MiniNaviKotlin\app\src\main\assets* to the asset folder of this app (*FollowMeSDKExample/app/src/main*).

## Installing the App

The App needs to be built by yourself. You can either download the project or create a pull requests, and open it in Android Studio.
Now that you have the code base you can adjust the code if wanted, and create an APK or build it directly on your device.

## Using the App

On startup you will see our Splash Screen while the SDK gets initialized. 
Currently there is no error-management, so in case of a licence error you need to look at the Android Studio's logcat. 
When the SDK initialized successful the main menu gets shown with currently only the option 'List'.  
On click on the list-button all available FollowMe-files (.nmea or .csv) will get parsed from the /user/followMeRoutes directory and are shown on screen. You can choose between local or routes from the FollowMe Server (check [FollowMeFileRepo.kt](app/src/main/java/de/infoware/followmesdkexample/followme/FollowMeFileRepo.kt)). In order to sync tours from the FollowMe server you will need to make sure that your device with the specific Hardware ID is within the correct Pot in the MapTrip Manager.
When you select a file a Dialog will pop up, asking if you want to start the route as a simulation or a normal navigation.  
Choosing either of those options will bring you to the CompanionMap, and the route will get calculated and started when it’s done.

### Example Video

![](readme_res/example_video.gif)

