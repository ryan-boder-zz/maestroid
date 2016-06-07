package name.ryanboder.maestroid

import android.graphics.Color
import org.scaloid.common._

class MainActivity extends SActivity with TagUtil {
  lazy val accelerometer = new Accelerometer(this)
  lazy val recorder = new AccelerometerRecorder()
  var recordButton: SButton = null

  onCreate {
    info("onCreate")
    contentView = new SVerticalLayout {
      STextView("Welcome to Maestroid")
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
      recordButton.setBackgroundColor(Color.DKGRAY)
      toast("Finished recording")
    } else {
      recorder.start()
      recordButton.setBackgroundColor(Color.RED)
      toast("Starting to record")
    }
  }
}
