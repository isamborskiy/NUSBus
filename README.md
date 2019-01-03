# NUSBus app [![Build Status](https://travis-ci.org/isamborskiy/NUSBus.svg?branch=master)](https://travis-ci.org/isamborskiy/NUSBus)
Open source Android app for NUS shuttle buses. 

## App information
* **Requires Android**: 5.0 and up
* **Current versions**: 1.0

## Screenshots
[<img src="images/main_screen.jpg" width=200>](images/main_screen.jpg)
[<img src="images/main_screen_route.jpg" width=200>](images/main_screen_route.jpg)
[<img src="images/main_screen_offline.jpg" width=200>](images/main_screen_offline.jpg)

## How to build APK?
1. Clone this repository
2. Generate "Maps SDK for Android" API key for GoogleMaps (find more information about it [here](https://developers.google.com/maps/documentation/javascript/get-api-key))
3. Put generated API key into `app/src/debug/res/values/google_maps_api.xml`
4. Run the following gradle task to assemble debug APK: `./gradlew assembleDebug`
5. Assembled APK file will be located here: `app/build/outputs/apk/debug/app-debug.apk`

#### Optional
To turn off LeakCanary you can go to [NusBusApplication.kt](https://github.com/isamborskiy/NUSBus/blob/master/app/src/main/kotlin/io.samborskii.nusbus/NusBusApplication.kt) 
and comment the following lines in `onCreate` method:
```kotlin
if (LeakCanary.isInAnalyzerProcess(this)) return
LeakCanary.install(this)
```

## Used technologies
App is implemented in [Kotlin language](https://kotlinlang.org/) with using the following libraries:
* [GoogleMaps](https://developers.google.com/maps/documentation/android-sdk)
* [Retrofit2](https://square.github.io/retrofit/)
* [Dagger2](https://google.github.io/dagger/)
* [Room](https://developer.android.com/topic/libraries/architecture/room)
* [RxPM](https://github.com/dmdevgo/RxPM)
* [LeakCanary](https://github.com/square/leakcanary)
