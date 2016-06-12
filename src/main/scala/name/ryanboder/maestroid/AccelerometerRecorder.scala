package name.ryanboder.maestroid

import java.io.{BufferedWriter, File, FileWriter, PrintWriter}
import java.text.SimpleDateFormat
import java.util.Date

import org.scaloid.common._

class AccelerometerRecorder extends TagUtil {
  var file: File = null
  var writer: PrintWriter = null

  def start(): Unit = {
    file = new File("/sdcard/Sensors/Accel-" + new SimpleDateFormat("yyyyMMdd-hhmmss'.csv'").format(new Date()))
    info("Writing accelerometer events to " + file.getAbsolutePath)
    if (!file.exists)
      file.createNewFile()
    writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))
  }

  def apply(data: AccelerometerData): AccelerometerData = {
    if (writer != null) {
      val a = data.acceleration
      val g = data.gravity
      val angle = data.acceleration angle data.gravity
      writer.println(s"${data.timestamp},${a.x},${a.y},${a.z},${g.x},${g.y},${g.z},${a.magnitude},$angle")
    }
    data
  }

  def isRecording(): Boolean = writer != null

  def stop(): Unit = {
    if (writer != null) {
      info("Closing " + file.getAbsolutePath)
      writer.close()
      writer = null
      file = null
    }
  }

}
