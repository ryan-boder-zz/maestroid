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

  it should "not throw exception when new sensor data is available before activate" in {
    subject.onSensorChanged(createSensorEvent())
  }

  it should "provide data when active and new sensor data is available" in {
    var callbackWasCalled = false
    val expectedData = AccelerometerData(81, 99.0f, 44.0f, 50.0f)
    subject.activate((data: AccelerometerData) => {
      callbackWasCalled = true
      assert(data == expectedData)
    })
    subject.onSensorChanged(createSensorEvent(expectedData))
    assert(callbackWasCalled)
  }

  it should "stop providing data when deactivated after being active" in {
    var callbackWasCalled = false
    subject.activate((data: AccelerometerData) => {
      callbackWasCalled = true
    })
    subject.deactivate
    subject.onSensorChanged(createSensorEvent())
    assert(!callbackWasCalled)
  }

  private def createSensorEvent(data: AccelerometerData): SensorEvent = {
    val sensorEvent = ReflectionHelpers.callConstructor(classOf[SensorEvent], ClassParameter.from(Integer.TYPE, 3))
    sensorEvent.timestamp = data.timestamp
    sensorEvent.values(0) = data.x
    sensorEvent.values(1) = data.y
    sensorEvent.values(2) = data.z
    return sensorEvent
  }

  private def createSensorEvent(): SensorEvent = {
    return createSensorEvent(AccelerometerData(0, 0.0f, 0.0f, 0.0f))
  }

}
