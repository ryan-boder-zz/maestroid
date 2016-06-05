package name.ryanboder.maestroid

import android.app.Activity
import android.os.Bundle

class MainActivity extends Activity with TypedFindView {
  lazy val textview = findView(TR.text)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
    textview.setText("Hello world, from Maestroid")
  }
}
