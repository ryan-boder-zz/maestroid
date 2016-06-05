package name.ryanboder.maestroid

import android.content.Context
import android.hardware.{Sensor, SensorEvent, SensorManager}
import org.mockito.Mockito._
import org.robolectric.annotation.Config
import org.robolectric.util.ReflectionHelpers
import org.robolectric.util.ReflectionHelpers.ClassParameter
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

  it should "register for sensor data stream when activated" in {
    subject.activate((data: AccelerometerData) => {})
    verify(sensorManager).registerListener(subject, sensor, SensorManager.SENSOR_DELAY_NORMAL)
  }

  it should "unregister from sensor data stream when deactivated" in {
    subject.deactivate
    verify(sensorManager).unregisterListener(subject)
  }

  it should "provide data when new sensor data is available" in {
    var callbackWasCalled = false
    val expectedData = AccelerometerData(81, 99.0f, 44.0f, 50.0f)
    subject.activate((data: AccelerometerData) => {
      callbackWasCalled = true
      assert(data == expectedData)
    })
    val sensorEvent = ReflectionHelpers.callConstructor(classOf[SensorEvent], ClassParameter.from(Integer.TYPE, 3))
    sensorEvent.timestamp = expectedData.timestamp
    sensorEvent.values(0) = expectedData.x
    sensorEvent.values(1) = expectedData.y
    sensorEvent.values(2) = expectedData.z
    subject.onSensorChanged(sensorEvent)
    assert(callbackWasCalled)
  }

}
