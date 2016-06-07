package name.ryanboder.maestroid

import org.scalatest.{BeforeAndAfter, FlatSpec}

class GestureDetectorSpec extends FlatSpec with BeforeAndAfter {

  behavior of "GestureDetector"

  var subject: GestureDetector = null

  before {
    subject = new GestureDetector
  }

  it should "not detect any gestures on the first sensor data" in {
    assert(subject(AccelerometerData(1L, 0.0f, 0.0f, 0.0f)) == List())
  }

}
