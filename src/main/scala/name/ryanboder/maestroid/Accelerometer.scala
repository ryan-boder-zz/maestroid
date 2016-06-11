package name.ryanboder.maestroid

import android.content.Context
import android.hardware.{Sensor, SensorEvent, SensorEventListener, SensorManager}
import org.scaloid.common._

class Accelerometer(context: Context) extends SensorEventListener with TagUtil {
  private var sensorManager: SensorManager = null
  private var accelerationSensor: Sensor = null
  private var gravitySensor: Sensor = null
  private var callback: AccelerometerData => Unit = null
  private var gravity: Vector3D = null

  context.getSystemService(Context.SENSOR_SERVICE) match {
    case sensorManager: SensorManager => this.sensorManager = sensorManager
    case _ => error("getSystemService returned something not a SensorManager!")
  }

  if (sensorManager != null) {
    accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    if (accelerationSensor == null)
      warn("No default acceleration sensor found on this device")
    gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    if (gravitySensor == null)
      warn("No default gravity sensor found on this device")
  }

  def isReady: Boolean = sensorManager != null && accelerationSensor != null && gravitySensor != null

  def activate(callback: AccelerometerData => Unit): Unit = {
    if (isReady) {
      info("Activating")
      this.callback = callback
      sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL)
      sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }
  }

  def deactivate(): Unit = {
    if (isReady) {
      info("Deactivating")
      sensorManager.unregisterListener(this)
      this.callback = null
      gravity = null
    }
  }

  override def onSensorChanged(event: SensorEvent): Unit = {
    if (event.sensor == gravitySensor) {
      gravity = Vector3D(event.values(0), event.values(1), event.values(2))
    } else if (event.sensor == accelerationSensor) {
      if (callback != null && gravity != null) {
        val acceleration = Vector3D(event.values(0), event.values(1), event.values(2))
        callback(AccelerometerData(event.timestamp, acceleration, gravity))
      }
    }
  }

  override def onAccuracyChanged(sensor: Sensor, accuracy: Int): Unit = {
    info(sensor.getStringType + " accuracy changed to " + accuracy)
  }
}

case class AccelerometerData(timestamp: Long,
                             acceleration: Vector3D,
                             gravity: Vector3D)
