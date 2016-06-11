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

  it should "not detect a tempo beat when direction doesn't change much" in {
    subject(AccelerometerData(ms(100), 20.0f, 0.0f, 0.0f))
    subject(AccelerometerData(ms(120), 13.0f, 3.0f, 0.0f))
    subject(AccelerometerData(ms(140), 15.0f, 0.0f, 0.0f))
    subject(AccelerometerData(ms(160), 19.0f, 4.0f, 0.0f))
    subject(AccelerometerData(ms(180), 13.0f, 0.0f, 0.0f))
    assert(!subject.detectTempoBeat())
  }

  ignore should "detect a tempo change when time since last beat differs from current tempo " in {
    val gestures = runFromCsvFile(subject, "Accel-1BPS.csv")
    assert(gestures.length == 5)
  }

  private def runFromCsvFile(detector: GestureDetector, fileName: String): List[Gesture] = {
    val source = io.Source.fromFile("src/test/resources/" + fileName)
    (for (line <- source.getLines() if line.trim.length > 0) yield {
      val columns = line.split(",").map(_.trim)
      val timestamp = columns(0).toLong
      val x = columns(1).toFloat
      val y = columns(2).toFloat
      val z = columns(3).toFloat
      detector(AccelerometerData(timestamp, x, y, z))
    }).flatten.toList
  }

}
