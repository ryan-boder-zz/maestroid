package name.ryanboder.maestroid

import android.content.Context
import org.scaloid.common._

import scala.collection.mutable.ListBuffer
import scala.math._

class GestureDetector(context: Context) extends TagUtil {
  implicit val ctx = context

  var history = ListBuffer[GestureHistoryItem]()
  val historySize = 4

  var tempo = 0.0f
  var lastTempoBeat = toNanoseconds(-1)

  val activateMagnitudeThreshold = 16
  val remainActiveMagnitudeThreshold = 4
  val activeTimeLimit = toNanoseconds(4)
  var lastMagnitudeAboveThreshold = toNanoseconds(-1)

  def apply(data: AccelerometerData): List[Gesture] = {
    updateHistory(data)

    if (detectTempoChange())
      return List(Tempo(tempo))

    List()
  }

  def detectTempoChange(): Boolean = {
    val event = history(0)
    val previousTempo = tempo
    if (detectTempoBeat()) {
      info("Beat detected at " + event.data.timestamp)
      //            if (lastTempoBeat != -1) {
      //              val secondsSinceLastBeat = (event.data.timestamp - lastTempoBeat) / 1000000000.0
      //              tempo = 1.0f / secondsSinceLastBeat.toFloat
      //            }
      //            lastTempoBeat = event.data.timestamp
    }
    //    abs(tempo - previousTempo) > 0.2 || previousTempo <= 0.0 && tempo > 0.0
    false // XXX
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

  def isActive: Boolean = history.length > 0 && (history(0).data.timestamp - lastMagnitudeAboveThreshold) < activeTimeLimit

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
}

sealed trait Gesture

case class Tempo(beatsPerSecond: Double) extends Gesture

case class Amplitude(meters: Double) extends Gesture

case class Length(meters: Double) extends Gesture

class GestureHistoryItem(val data: AccelerometerData) {
  val angleToGravity = data.acceleration angle data.gravity
}
