package name.ryanboder.maestroid

import android.content.Context
import android.hardware.{Sensor, SensorEvent, SensorEventListener, SensorManager}
import org.scaloid.common._

class Accelerometer(context: Context) extends SensorEventListener with TagUtil {
  private var sensorManager: SensorManager = null
  private var accelerometer: Sensor = null
  private var callback: AccelerometerData => Unit = null

  context.getSystemService(Context.SENSOR_SERVICE) match {
    case sensorManager: SensorManager => this.sensorManager = sensorManager
    case _ => error("getSystemService returned something not a SensorManager!")
  }

  if (sensorManager != null) {
    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    if (accelerometer == null)
      warn("No default accelerometer found on this device")
  }

  info("Accelerometer min delay is " + accelerometer.getMinDelay)

  def isReady: Boolean = sensorManager != null && accelerometer != null

  def activate(callback: AccelerometerData => Unit): Unit = {
    info("Activated")
    if (sensorManager != null && accelerometer != null) {
      this.callback = callback
      sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }
  }

  def deactivate(): Unit = {
    info("Deactivated")
    if (sensorManager != null && accelerometer != null) {
      sensorManager.unregisterListener(this)
      this.callback = null
    }
  }

  override def onSensorChanged(event: SensorEvent): Unit = {
    if (callback != null) {
      callback(AccelerometerData(event.timestamp, event.values(0), event.values(1), event.values(2)))
    }
  }

  override def onAccuracyChanged(sensor: Sensor, accuracy: Int): Unit = {
    info("Accelerometer accuracy changed to " + accuracy)
  }

}

case class AccelerometerData(timestamp: Long,
                             x: Float,
                             y: Float,
                             z: Float)
