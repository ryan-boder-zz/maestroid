package name.ryanboder.maestroid

import android.content.Context
import org.scaloid.common._

import scala.collection.mutable.ListBuffer
import scala.math._

class GestureDetector(context: Context) extends TagUtil {
  implicit val ctx = context

  var history = ListBuffer[GestureHistoryItem]()
  val historySize = 4

  val tempoChangeThreshold = 0.1
  var tempo = 0.0
  var lastTempoBeat = -1L

  val activateMagnitudeThreshold = 16
  val remainActiveMagnitudeThreshold = 4
  val activeTimeLimit = toNanoseconds(4)
  var lastMagnitudeAboveThreshold = -1L

  def apply(data: AccelerometerData): List[Gesture] = {
    updateHistory(data)

    if (detectTempoChange())
      return List(Tempo(tempo))

    List()
  }

  def detectTempoChange(): Boolean = {
    val event = history.head
    val previousTempo = tempo
    if (detectTempoBeat()) {
      info("Beat detected at " + event.data.timestamp)
      if (lastTempoBeat != -1) {
        val secondsSinceLastBeat = fromNanoseconds(event.data.timestamp - lastTempoBeat)
        tempo = 1.0 / secondsSinceLastBeat
      }
      lastTempoBeat = event.data.timestamp
    }
    abs(tempo - previousTempo) > tempoChangeThreshold || previousTempo <= 0.0 && tempo > 0.0
  }

  def detectTempoBeat(): Boolean = {
    if (history.size < historySize)
      return false
    if (!isActive)
      return false
    val crossedAngleThreshold = history(0).angleToGravity > Pi / 2 &&
      history(1).angleToGravity > Pi / 2 &&
      history(2).angleToGravity <= Pi / 2 &&
      history(3).angleToGravity <= Pi / 2
    crossedAngleThreshold
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
        println("Activated GestureDetector")
        lastMagnitudeAboveThreshold = data.timestamp
      }
    }
  }

  private def toNanoseconds(s: Double): Long = s.toLong * 1000000000

  private def fromNanoseconds(ns: Long): Double = ns.toDouble / 1000000000.0
}

sealed trait Gesture

case class Tempo(beatsPerSecond: Double) extends Gesture

case class Amplitude(meters: Double) extends Gesture

case class Length(meters: Double) extends Gesture

class GestureHistoryItem(val data: AccelerometerData) {
  val angleToGravity = data.acceleration angle data.gravity
}
