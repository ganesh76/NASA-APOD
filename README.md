# NASA-APOD

# Notes  
* Used MVVM architecture with kotlin.
* Used Retrofit library to fetch the apod image and stored in Room DB for offline access.
* Story implementation meets the acceptance criteria.
* The UI is kept simple meeting the story expectations.
* APK can be downloaded at [APK](app-release.apk)

# Improvement Areas
* We can use dependency injection framework such as Koin to separate the dependencies in the app.
* We can also separate the DB and Rest Api as different modules in the app.
* Api has response of media type video which gives video url instead of image url, through api I saw we can get the thumbnail of the video too, used this as a workaround.
* The Api response has date of different time zone, probably its USA, had to compare with local device date to update the picture for the day.

# Screenshots
<img align="left" src="apod_loaded.png" width="240"> <img align="left" src="apod_with_alert_msg.png" width="240">  



