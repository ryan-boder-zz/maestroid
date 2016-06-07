package name.ryanboder.maestroid

import org.scaloid.common._

class MainActivity extends SActivity with TagUtil {
  lazy val accelerometer = new Accelerometer(this)
  lazy val recorder = new AccelerometerRecorder()

  onCreate {
    info("onCreate")
    contentView = new SVerticalLayout {
      STextView("Welcome to Maestroid")
      SButton("Record Accelerometer", toggleAccelerometerRecording)
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
    })
  }

  override def onPause() {
    info("onPause")
    accelerometer.deactivate()
    super.onPause()
  }

  private def toggleAccelerometerRecording(): Unit = {
    if (recorder.isRecording) {
      recorder.stop()
      toast("Finished recording")
    } else {
      recorder.start()
      toast("Starting to record")
    }
  }
}
