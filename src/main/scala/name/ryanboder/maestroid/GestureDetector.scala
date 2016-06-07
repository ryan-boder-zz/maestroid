package name.ryanboder.maestroid

class GestureDetector {

  def apply(data: AccelerometerData): List[Gesture] = {
    return List()
  }

}

sealed trait Gesture

case class Tempo(beatsPerSecond: Int) extends Gesture

case class Amplitude(meters: Float) extends Gesture

case class Length(meters: Float) extends Gesture
