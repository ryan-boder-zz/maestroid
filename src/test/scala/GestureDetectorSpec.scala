package name.ryanboder.maestroid

import org.scalatest.{BeforeAndAfter, FlatSpec}

class GestureDetectorSpec extends FlatSpec with BeforeAndAfter {
  def ms(x: Long): Long = x * 1000000

  behavior of "GestureDetector"

  var subject: GestureDetector = null

  before {
    subject = new GestureDetector
  }

  it should "not detect any gestures on the first sensor data" in {
    assert(subject(AccelerometerData(1L, 0.0f, 0.0f, 0.0f)) == List())
  }

  it should "detect a tempo beat when direction changes drastically from down to up within 80ms" in {
    subject(AccelerometerData(ms(100), 20.0f, 0.0f, 0.0f))
    subject(AccelerometerData(ms(120), 20.0f, 0.0f, 0.0f))
    subject(AccelerometerData(ms(140), 0.0f, 0.0f, 0.0f))
    subject(AccelerometerData(ms(160), -15.0f, 0.0f, 0.0f))
    subject(AccelerometerData(ms(180), -15.0f, 0.0f, 0.0f))
    assert(subject.detectTempoBeat())
  }

}
