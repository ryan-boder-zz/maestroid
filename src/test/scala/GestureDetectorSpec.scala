package name.ryanboder.maestroid

import org.scalatest.{BeforeAndAfter, FlatSpec}

class GestureDetectorSpec extends FlatSpec with BeforeAndAfter {
  behavior of "GestureDetector"

  var subject: GestureDetector = null

  before {
    subject = new GestureDetector
  }

  it should "not detect any gestures on the first sensor data" in {
    assert(subject(AccelerometerData(1L, Vector3D(0.0, 0.0, 0.0), Vector3D(0.0, 0.0, 0.0))) == List())
  }

  it should "detect a tempo beat when direction changes drastically from down to up within 80ms" in {
    subject(AccelerometerData(ms(100), Vector3D(20.0, 0.0, 0.0), Vector3D(9.8, 0.0, 0.0)))
    subject(AccelerometerData(ms(120), Vector3D(20.0, 0.0, 0.0), Vector3D(9.8, 0.0, 0.0)))
    subject(AccelerometerData(ms(140), Vector3D(0.0, 0.0, 0.0), Vector3D(9.8, 0.0, 0.0)))
    subject(AccelerometerData(ms(160), Vector3D(-15.0, 0.0, 0.0), Vector3D(9.8, 0.0, 0.0)))
    subject(AccelerometerData(ms(180), Vector3D(-15.0, 0.0, 0.0), Vector3D(9.8, 0.0, 0.0)))
    assert(subject.detectTempoBeat())
  }

  it should "not detect a tempo beat when direction doesn't change much" in {
    subject(AccelerometerData(ms(100), Vector3D(20.0, 0.0, 0.0), Vector3D(9.8, 0.0, 0.0)))
    subject(AccelerometerData(ms(120), Vector3D(13.0, 3.0, 0.0), Vector3D(9.8, 0.0, 0.0)))
    subject(AccelerometerData(ms(140), Vector3D(15.0, 0.0, 0.0), Vector3D(9.8, 0.0, 0.0)))
    subject(AccelerometerData(ms(160), Vector3D(19.0, 4.0, 0.0), Vector3D(9.8, 0.0, 0.0)))
    subject(AccelerometerData(ms(180), Vector3D(13.0, 0.0, 0.0), Vector3D(9.8, 0.0, 0.0)))
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
      val acceleration = Vector3D(columns(1).toDouble, columns(2).toDouble, columns(3).toDouble)
      val gravity = Vector3D(columns(4).toDouble, columns(5).toDouble, columns(6).toDouble)
      detector(AccelerometerData(timestamp, acceleration, gravity))
    }).flatten.toList
  }

  def ms(x: Long): Long = x * 1000000
}
