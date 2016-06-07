package name.ryanboder.maestroid

class GestureDetector {

  def apply(data: AccelerometerData): List[Gesture] = {
    return List()
  }

}

sealed trait Gesture

case class Tempo(value: Float) extends Gesture

case class Amplitude(value: Float) extends Gesture

case class Length(value: Float) extends Gesture
