package name.ryanboder.maestroid

import scala.collection.mutable.ListBuffer

class GestureDetector {

  var history = ListBuffer[GestureHistoryItem]()
  val maxHistorySize = 5

  def apply(data: AccelerometerData): List[Gesture] = {
    updateHistory(data)

    List()
  }

  def updateHistory(data: AccelerometerData): Unit = {
    history.prepend(new GestureHistoryItem(data))
    if (history.size > maxHistorySize)
      history.remove(history.size - 1)
  }

}

sealed trait Gesture

case class Tempo(beatsPerSecond: Int) extends Gesture

case class Amplitude(meters: Float) extends Gesture

case class Length(meters: Float) extends Gesture
