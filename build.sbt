androidBuild

javacOptions in Compile ++= "-source" :: "1.7" :: "-target" :: "1.7" :: Nil

scalaVersion := "2.11.8"
minSdkVersion in Android := "23"
