package name.ryanboder.maestroid

import java.lang.Math._

class GestureHistoryItem(data: AccelerometerData) {
  val timestamp = data.timestamp
  val x = data.x
  val y = data.y
  val z = data.z
  val magnitude = sqrt(x * x + y * y + z * z)

  def dot(that: GestureHistoryItem): Double = {
    this.x * that.x + this.y * that.y + this.z + that.z
  }

  def angle(that: GestureHistoryItem): Double = {
    acos((this dot that) / (this.magnitude * that.magnitude))
  }

}
