package name.ryanboder.maestroid

import android.content.Context
import android.hardware.{Sensor, SensorEvent, SensorEventListener, SensorManager}
import org.scaloid.common._


class Accelerometer(context: Context) extends SensorEventListener with TagUtil {
  implicit val tag = LoggerTag("Maestroid/" + getClass.getSimpleName)

  private var sensorManager: SensorManager = null
  private var accelerometer: Sensor = null

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

  def activate: Unit = {
    if (sensorManager != null && accelerometer != null)
      sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
  }

  def deactivate: Unit = {
    if (sensorManager != null && accelerometer != null)
      sensorManager.unregisterListener(this)
  }

  override def onSensorChanged(event: SensorEvent): Unit = {

  }

  override def onAccuracyChanged(sensor: Sensor, accuracy: Int): Unit = {

  }

}
