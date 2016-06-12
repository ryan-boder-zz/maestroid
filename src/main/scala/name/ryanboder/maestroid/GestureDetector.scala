package name.ryanboder.maestroid

import android.content.Context
import org.scaloid.common._

import scala.collection.mutable.ListBuffer
import scala.math._

class GestureDetector(context: Context) extends TagUtil {
  implicit val ctx = context

  var history = ListBuffer[GestureHistoryItem]()
  val historySize = 10

  val tempoChangeThreshold = 0.1
  var tempo = 0.0
  var lastTempoBeat = -1L

  val activateMagnitudeThreshold = 16
  val remainActiveMagnitudeThreshold = 4
  val activeTimeLimit = toNanoseconds(4)
  var lastMagnitudeAboveThreshold = -1L

  val volumeChangeThreshold = 0.05
  var volume = 0.0
  var maxMagnitude = 0.0

  def apply(data: AccelerometerData): List[Gesture] = {
    updateHistory(data)

    val gestures = ListBuffer[Gesture]()
    if (detectTempoChange())
      gestures.append(Tempo(tempo))
    if (detectVolumeChange())
      gestures.append(Volume(volume))

    gestures.toList
  }

  def detectTempoChange(): Boolean = {
    val now = history.head
    val previousTempo = tempo
    if (detectTempoBeat()) {
      info("Beat detected at " + now.data.timestamp)
      if (lastTempoBeat != -1) {
        val secondsSinceLastBeat = fromNanoseconds(now.data.timestamp - lastTempoBeat)
        tempo = 1.0 / secondsSinceLastBeat
      }
      lastTempoBeat = now.data.timestamp
    }
    abs(tempo - previousTempo) > tempoChangeThreshold || previousTempo <= 0.0 && tempo > 0.0
  }

  def detectTempoBeat(): Boolean = {
    if (!isActive || history.size < 4)
      return false
    val crossedAngleThreshold = history.head.angleToGravity > Pi / 2 &&
      history(1).angleToGravity > Pi / 2 &&
      history(2).angleToGravity <= Pi / 2 &&
      history(3).angleToGravity <= Pi / 2
    crossedAngleThreshold
  }

  def detectVolumeChange(): Boolean = {
    if (!isActive || history.size < historySize)
      return false
    val maxRecentMagnitude = history.map(_.data.acceleration.magnitude).max
    if (maxRecentMagnitude > maxMagnitude) {
      maxMagnitude = maxRecentMagnitude
    }
    val previousVolume = volume
    volume = maxRecentMagnitude / maxMagnitude
    volume - previousVolume > volumeChangeThreshold || previousVolume <= 0.0 && volume > 0.0
  }

  def isActive: Boolean = history.nonEmpty && (history.head.data.timestamp - lastMagnitudeAboveThreshold) < activeTimeLimit

  def updateHistory(data: AccelerometerData): Unit = {
    history.prepend(new GestureHistoryItem(data))
    if (history.size > historySize)
      history.remove(history.size - 1)

    if (isActive) {
      if (data.acceleration.magnitude > remainActiveMagnitudeThreshold) {
        lastMagnitudeAboveThreshold = data.timestamp
      }
    } else {
      if (data.acceleration.magnitude > activateMagnitudeThreshold) {
        info("Activated GestureDetector")
        lastMagnitudeAboveThreshold = data.timestamp
      }
    }
  }

  private def toNanoseconds(s: Double): Long = s.toLong * 1000000000

  private def fromNanoseconds(ns: Long): Double = ns.toDouble / 1000000000.0
}

sealed trait Gesture

case class Tempo(beatsPerSecond: Double) extends Gesture

case class Volume(volume: Double) extends Gesture

case class Length(meters: Double) extends Gesture

class GestureHistoryItem(val data: AccelerometerData) {
  val angleToGravity = data.acceleration angle data.gravity
}
