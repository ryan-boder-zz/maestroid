package name.ryanboder.maestroid

import android.content.Context
import android.hardware.{Sensor, SensorManager}
import org.scaloid.common._


class Accelerometer(context: Context) extends TagUtil {
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

  def isReady = sensorManager != null && accelerometer != null
}
