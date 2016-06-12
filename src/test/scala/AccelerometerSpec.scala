package name.ryanboder.maestroid

import android.content.Context
import android.hardware.{Sensor, SensorEvent, SensorManager}
import org.mockito.Mockito._
import org.robolectric.annotation.Config
import org.robolectric.util.ReflectionHelpers
import org.robolectric.util.ReflectionHelpers.ClassParameter
import org.scalactic.Tolerance._
import org.scalatest.{BeforeAndAfter, FlatSpec, RobolectricSuite}

@Config(sdk = Array(21))
class AccelerometerSpec extends FlatSpec with BeforeAndAfter with RobolectricSuite {
  behavior of "Accelerometer"

  var context: Context = null
  var sensorManager: SensorManager = null
  var accelerationSensor: Sensor = null
  var gravitySensor: Sensor = null
  var subject: Accelerometer = null

  before {
    context = mock(classOf[Context])
    sensorManager = mock(classOf[SensorManager])
    accelerationSensor = mock(classOf[Sensor])
    gravitySensor = mock(classOf[Sensor])
    when(context.getSystemService(Context.SENSOR_SERVICE)).thenReturn(sensorManager, Nil: _*)
    when(sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)).thenReturn(accelerationSensor)
    when(sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)).thenReturn(gravitySensor)
    subject = new Accelerometer(context)
  }

  it should "find and use a system accelerometer when created" in {
    assert(subject.isReady)
  }

  it should "register for sensor data stream when activated" in {
    subject.activate((data: AccelerometerData) => {})
    verify(sensorManager).registerListener(subject, gravitySensor, subject.requestedSensorFrequencyUs)
    verify(sensorManager).registerListener(subject, accelerationSensor, subject.requestedSensorFrequencyUs)
  }

  it should "unregister from sensor data stream when deactivated" in {
    subject.deactivate()
    verify(sensorManager).unregisterListener(subject)
  }

  it should "not throw exception when new sensor data is available before activate" in {
    subject.onSensorChanged(createSensorEvent(accelerationSensor))
  }

  it should "provide data when active and new sensor data is available" in {
    var callbackWasCalled = false
    val expectedData = AccelerometerData(81, Vector3D(99.0, 44.0, 50.0), Vector3D(9.8, 0.0, 0.0))
    subject.activate((data: AccelerometerData) => {
      callbackWasCalled = true
      assert(tolerantEquals(data, expectedData))
    })
    subject.onSensorChanged(createSensorEvent(gravitySensor, expectedData.timestamp - 100, expectedData.gravity))
    subject.onSensorChanged(createSensorEvent(accelerationSensor, expectedData.timestamp, expectedData.acceleration))
    assert(callbackWasCalled)
  }

  it should "stop providing data when deactivated after being active" in {
    var callbackWasCalled = false
    subject.activate((data: AccelerometerData) => {
      callbackWasCalled = true
    })
    subject.deactivate()
    subject.onSensorChanged(createSensorEvent(gravitySensor))
    subject.onSensorChanged(createSensorEvent(accelerationSensor))
    assert(!callbackWasCalled)
  }

  private def createSensorEvent(sensor: Sensor, timestamp: Long, data: Vector3D): SensorEvent = {
    val sensorEvent = ReflectionHelpers.callConstructor(classOf[SensorEvent], ClassParameter.from(Integer.TYPE, 3))
    sensorEvent.sensor = sensor
    sensorEvent.timestamp = timestamp
    sensorEvent.values(0) = data.x.toFloat
    sensorEvent.values(1) = data.y.toFloat
    sensorEvent.values(2) = data.z.toFloat
    sensorEvent
  }

  private def createSensorEvent(sensor: Sensor): SensorEvent = {
    createSensorEvent(sensor, 0, Vector3D(0.0, 0.0, 0.0))
  }

  private def tolerantEquals(a: AccelerometerData, b: AccelerometerData): Boolean = {
    a.timestamp == b.timestamp && tolerantEquals(a.acceleration, b.acceleration) && tolerantEquals(a.gravity, b.gravity)
  }

  private def tolerantEquals(a: Vector3D, b: Vector3D): Boolean = {
    val tolerance = 0.0001
    a.x === b.x +- tolerance && a.y === b.y +- tolerance && a.z === b.z +- tolerance
  }
}
