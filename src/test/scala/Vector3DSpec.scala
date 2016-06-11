package name.ryanboder.maestroid

import org.scalactic.TolerantNumerics
import org.scalatest.FlatSpec

import scala.math._

class Vector3DSpec extends FlatSpec {
  implicit val doubleEquality = TolerantNumerics.tolerantDoubleEquality(0.01)

  behavior of "Vector3D"

  it should "calculate magnitude" in {
    val subject = Vector3D(1.0, 1.0, 1.0)
    assert(subject.magnitude === sqrt(3))
  }

  it should "calculate the dot product of 2 vectors" in {
    val a = Vector3D(1.0, 1.0, 1.0)
    val b = Vector3D(-1.0, -1.0, -1.0)
    val c = Vector3D(1.0, 1.0, 0.0)
    val d = Vector3D(1.0, -1.0, 0.0)
    assert((a dot a) === 3.0)
    assert((a dot b) === -3.0)
    assert((c dot d) === 0.0)
  }

  it should "calculate the angle between 2 vector" in {
    val a = Vector3D(1.0f, 1.0f, 1.0f)
    val c = Vector3D(1.0f, 1.0f, 0.0f)
    val d = Vector3D(1.0f, -1.0f, 0.0f)
    assert((a angle a) === 0.0)
    assert((c angle d) === Pi / 2.0)
  }

}
