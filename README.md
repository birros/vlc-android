# VLC for Android (with http server)

Fork of [VLC for Android](https://github.com/videolan/vlc-android) with http
server to expose playing status.

---

## How to fork VLC for Android

This doc describe how to fork vlc and don't collide with official vlc build on
Android.

## Get resources

```shell
$ docker pull registry.videolan.org:5000/vlc-debian-android:20190507015459
$ git clone --depth=1 -b 3.2.x https://github.com/birros/vlc-android.git
```

## Change applicationId and app_name

```shell
$ export NEW_APP_ID=com.github.birros.vlc
$ export NEW_APP_NAME="VLCWithServer"
$ export VLC_ANDROID_DIR=$PWD
$ # replace applicationId in files
$ LC_ALL=C find . -not -wholename "./.git*" -not -wholename "./README.md" -type f -exec sed -i '' s/org\\.videolan\\.vlc/$NEW_APP_ID/ {} +
$ # rename files depending on new applicationId
$ go run .fork/rename-files.go $VLC_ANDROID_DIR $NEW_APP_ID
$ # rename a file
$ mv vlc-android/assets/schemas/org.videolan.vlc.database.MediaDatabase vlc-android/assets/schemas/$NEW_APP_ID.database.MediaDatabase
$ # change app_name
$ sed -i "" "s/VLC/$NEW_APP_NAME/" vlc-android/res/values/strings.xml
```

## Remove applicationId suffix of debug build (optional)

```shell
$ sed -i '' 's/applicationIdSuffix ".debug"/\/\/ applicationIdSuffix ".debug"/' vlc-android/build.gradle
```

## Build debug apk

Notes:
* First build take around 20mn on Macbook.
* Docker can freeze during build so restart instance to build apk.

```shell
$ docker run --rm -ti \
    -v $PWD:/sources \
    -v $HOME/vlc-android-build-cache/home:/home/videolan \
    -v $HOME/vlc-android-build-cache/build-tools:/sdk/android-sdk-linux/build-tools \
    -v $HOME/vlc-android-build-cache/platforms:/sdk/android-sdk-linux/platforms \
    registry.videolan.org:5000/vlc-debian-android:20190507015459 bash
$ ./compile.sh --init
$ ./gradlew assembleDebug
```

## Resources

* https://code.videolan.org/videolan/docker-images/blob/ee87fcf18c22c33b6998275d80dd158949e0a847/vlc-debian-android/Dockerfile
* https://code.videolan.org/videolan/vlc-android/blob/3.2.6/.gitlab-ci.yml#L9
* https://code.videolan.org/videolan/vlc-android/blob/3.2.6/.gitlab-ci.yml#L59-60