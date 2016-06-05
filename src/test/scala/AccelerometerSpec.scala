package name.ryanboder.maestroid

import android.content.Context
import android.hardware.{Sensor, SensorManager}
import org.mockito.Mockito._
import org.robolectric.annotation.Config
import org.scalatest.{BeforeAndAfter, FlatSpec, RobolectricSuite}

@Config(sdk = Array(21))
class AccelerometerSpec extends FlatSpec with BeforeAndAfter with RobolectricSuite {

  behavior of "Accelerometer"

  var context: Context = null
  var sensorManager: SensorManager = null
  var sensor: Sensor = null

  var subject: Accelerometer = null

  before {
    context = mock(classOf[Context])
    sensorManager = mock(classOf[SensorManager])
    sensor = mock(classOf[Sensor])
    when(context.getSystemService(Context.SENSOR_SERVICE)).thenReturn(sensorManager, Nil: _*)
    when(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)).thenReturn(sensor)
    subject = new Accelerometer(context)
  }

  it should "find and use a system accelerometer when created" in {
    assert(subject.isReady)
  }

}
