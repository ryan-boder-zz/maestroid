package name.ryanboder.maestroid

import java.lang.Math._

import org.scalactic.TolerantNumerics
import org.scalatest.FlatSpec

class GestureHistoryItemSpec extends FlatSpec {
  val doubleEquality = TolerantNumerics.tolerantDoubleEquality(0.01)

  behavior of "GestureHistoryItem"

  it should "calculate magnitude on creation" in {
    val subject = new GestureHistoryItem(AccelerometerData(0, 1.0f, 1.0f, 1.0f))
    assert(subject.magnitude === sqrt(3))
  }

}
