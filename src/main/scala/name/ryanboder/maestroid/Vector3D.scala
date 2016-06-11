package name.ryanboder.maestroid

import scala.math._

case class Vector3D(x: Double, y: Double, z: Double) {

  val magnitude = sqrt(x * x + y * y + z * z)

  def dot(that: Vector3D): Double = {
    this.x * that.x + this.y * that.y + this.z * that.z
  }

  def angle(that: Vector3D): Double = {
    var cosine = (this dot that) / (this.magnitude * that.magnitude)

    // Make sure precision error didn't get out of the valid cosine range
    if (cosine > 1.0)
      cosine = 1.0
    if (cosine < -1.0)
      cosine = -1.0

    acos(cosine)
  }

}
