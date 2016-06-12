package name.ryanboder.maestroid

import java.io.File

import android.content.Context
import android.media.{MediaPlayer, PlaybackParams}
import android.net.Uri
import org.scaloid.common._


class MusicPlayer(context: Context) extends TagUtil {
  val audioFilePath = "/sdcard/Music/Conductor.mp3"
  info("Playing " + audioFilePath)

  val audioFile = new File(audioFilePath)
  val player = MediaPlayer.create(context, Uri.fromFile(audioFile))

  def apply(gesture: Gesture): Unit = {
    gesture match {
      case t: Tempo => changeTempo(t)
      case v: Volume => changeVolume(v)
      case g: Gesture => error("Unknown gesture " + g)
    }
  }

  private def changeTempo(tempo: Tempo): Unit = {
    if (!player.isPlaying) {
      player.start()
    }
    info("Setting speed to " + tempo.beatsPerSecond)
    val params = new PlaybackParams()
    params.setSpeed(tempo.beatsPerSecond.toFloat)
    player.setPlaybackParams(params)
  }

  private def changeVolume(v: Volume): Unit = {
    if (!player.isPlaying)
      return
    info("Setting volume to " + v)
    player.setVolume(v.volume.toFloat, v.volume.toFloat)
  }
}
