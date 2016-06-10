package name.ryanboder.maestroid

import org.scalactic.TolerantNumerics
import org.scalatest.FlatSpec

import scala.math._

class GestureHistoryItemSpec extends FlatSpec {
  val doubleEquality = TolerantNumerics.tolerantDoubleEquality(0.01)

  behavior of "GestureHistoryItem"

  it should "calculate magnitude on creation" in {
    val subject = new GestureHistoryItem(AccelerometerData(0, 1.0f, 1.0f, 1.0f))
    assert(subject.magnitude === sqrt(3))
  }

  it should "calculate the dot product between vectors" in {
    val a = new GestureHistoryItem(AccelerometerData(0, 1.0f, 1.0f, 1.0f))
    val b = new GestureHistoryItem(AccelerometerData(0, -1.0f, -1.0f, -1.0f))
    val c = new GestureHistoryItem(AccelerometerData(0, 1.0f, 1.0f, 0.0f))
    val d = new GestureHistoryItem(AccelerometerData(0, 1.0f, -1.0f, 0.0f))

    assert((a dot a) === 3.0)
    assert((a dot b) === -3.0)
    assert((c dot d) === 0.0)
  }

  it should "calculate the angle between vectors" in {
    val a = new GestureHistoryItem(AccelerometerData(0, 1.0f, 1.0f, 1.0f))
    val c = new GestureHistoryItem(AccelerometerData(0, 1.0f, 1.0f, 0.0f))
    val d = new GestureHistoryItem(AccelerometerData(0, 1.0f, -1.0f, 0.0f))

    assert((a angle a) === 0.0)
    assert((c angle d) === Pi / 2.0)
  }

}
