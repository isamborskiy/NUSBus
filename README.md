# NUSBus app
Open source Android app for NUS shuttle buses. 

## Screenshots
[<img src="images/main_screen.jpg" width=200>](images/main_screen.jpg)

## Build instruction
1. Clone this repository
2. Generate "Maps SDK for Android" API key for GoogleMaps (find more information about it [here](https://developers.google.com/maps/documentation/javascript/get-api-key))
3. Put generated API key into `app/src/debug/res/values/google_maps_api.xml`
4. Run the following gradle task to assemble debug APK:
  ```bash
  ./gradlew :assembleDebug
  ```
5. Assembled APK file will be located here: `app/build/outputs/apk/debug/app-debug.apk`
