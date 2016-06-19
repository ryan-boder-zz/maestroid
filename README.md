# Maestroid

Uses the motion sensors on your android device so you can control music by swinging your device in a U-shape motion like you're conducting a symphony.

Maestroid was originally written as example code for a [presentation](https://github.com/ryan-boder/scala-on-android).

## Build & Install

We use the [sbt-android plugin](https://github.com/scala-android/sbt-android) so the sbt commands described there should work.

Since this project is in an early, experimental stage, if you're installing it then you probably want to develop it. To develop use:

```scala
// Run tests
sbt test

// Build APK and deploy to default device
sbt run // same as sbt android:run
```

If you want to just build an APK for installation use:

```scala
sbt android:package
```

Maestroid needs an mp3 on your file system called `/sdcard/Music/Maestroid.mp3` so you will need to install one there.

Requires Android SDK version 23 or higher and Android version 6 (Marshmallow) or higher so we can use [PlaybackParams](https://developer.android.com/reference/android/media/PlaybackParams.html).

## Usage

Once you have started Maestroid simply start conducting (swinging your device in a U-shape motion) and the music will start playing.

Each time you move the device down and then up Maestroid detects a beat. The tempo of the music is controlled by these beats.

The magnitude of acceleration controls the volume. The harder you swing the device the louder it gets, the slower you swing it the softer it gets.

TODO: pitch control

## Development

Maestroid is written in [Scala](http://www.scala-lang.org/documentation/getting-started.html), not Java.

You must have [SBT](http://www.scala-sbt.org/0.13/docs/index.html) installed.

```
brew install sbt
```

### Other Documentation

* [sbt-android plugin](https://github.com/scala-android/sbt-android)
* [Scaloid](https://github.com/pocorall/scaloid)
