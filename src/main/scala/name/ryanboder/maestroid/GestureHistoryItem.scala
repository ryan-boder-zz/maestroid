package name.ryanboder.maestroid

import scala.math._

class GestureHistoryItem(data: AccelerometerData) {
  val timestamp = data.timestamp
  val x = data.x
  val y = data.y
  val z = data.z
  val magnitude = sqrt(x * x + y * y + z * z)

  def dot(that: GestureHistoryItem): Double = {
    this.x * that.x + this.y * that.y + this.z * that.z
  }

  def angle(that: GestureHistoryItem): Double = {
    var cosine = (this dot that) / (this.magnitude * that.magnitude)

    // Make sure precision error didn't get out of the valid cosine range
    if (cosine > 1.0)
      cosine = 1.0
    if (cosine < -1.0)
      cosine = -1.0

    acos(cosine)
  }

}
