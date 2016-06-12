package name.ryanboder.maestroid

import android.graphics.Color
import org.scaloid.common._

class MainActivity extends SActivity with TagUtil {
  lazy val accelerometer = new Accelerometer(this)
  lazy val recorder = new AccelerometerRecorder()
  lazy val detector = new GestureDetector(this)
  lazy val player = new MusicPlayer(this)

  private var accelerometerViews: Array[STextView] = null
  private var recordButton: SButton = null

  onCreate {
    info("onCreate")
    contentView = new SVerticalLayout {
      STextView("Welcome to Maestroid")
      accelerometerViews = Array(STextView(), STextView(), STextView())
      recordButton = SButton("Record Accelerometer", toggleAccelerometerRecording).backgroundColor(Color.DKGRAY)
    }
  }

  onDestroy {
    info("onDestroy")
  }

  override def onResume() {
    super.onResume()
    info("onResume")
    accelerometer.activate((data: AccelerometerData) => {
      recorder(data)
      updateAccelerometerViews(data)
      for (gesture <- detector(data)) {
        info(gesture.toString)
        player(gesture)
      }
    })
  }

  override def onPause() {
    info("onPause")
    accelerometer.deactivate()
    super.onPause()
  }

  def updateAccelerometerViews(data: AccelerometerData): Unit = {
    if (data.timestamp - lastLoggedAccelerometerTime > 1000000000) {
      accelerometerViews(0).setText("X: " + data.acceleration.x)
      accelerometerViews(1).setText("Y: " + data.acceleration.y)
      accelerometerViews(2).setText("Z: " + data.acceleration.z)
      lastLoggedAccelerometerTime = data.timestamp
    }
  }

  private var lastLoggedAccelerometerTime = 0L

  private def toggleAccelerometerRecording(): Unit = {
    if (recorder.isRecording) {
      recorder.stop()
      recordButton.setBackgroundColor(Color.DKGRAY)
      toast("Finished recording")
    } else {
      recorder.start()
      recordButton.setBackgroundColor(Color.RED)
      toast("Starting to record")
    }
  }
}
