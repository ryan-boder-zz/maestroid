package name.ryanboder.maestroid

import scala.collection.mutable.ListBuffer
import scala.math._

class GestureDetector {

  var history = ListBuffer[GestureHistoryItem]()
  val maxHistorySize = 5
  var tempo = 0.0f
  var lastTempoBeat = -1L

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
      if (lastTempoBeat != -1) {
        val secondsSinceLastBeat = (event.data.timestamp - lastTempoBeat) / 1000000000.0
        tempo = 1.0f / secondsSinceLastBeat.toFloat
      }
      lastTempoBeat = event.data.timestamp
    }
    abs(tempo - previousTempo) > 0.2 || previousTempo <= 0.0 && tempo > 0.0
  }

  def detectTempoBeat(): Boolean = {
    history.size >= 4 && (history(0).data.acceleration angle history(3).data.acceleration) > (3 * Pi / 4)
  }

  def updateHistory(data: AccelerometerData): Unit = {
    history.prepend(new GestureHistoryItem(data))
    if (history.size > maxHistorySize)
      history.remove(history.size - 1)
  }

}

sealed trait Gesture

case class Tempo(beatsPerSecond: Float) extends Gesture

case class Amplitude(meters: Float) extends Gesture

case class Length(meters: Float) extends Gesture

class GestureHistoryItem(val data: AccelerometerData) {
}
