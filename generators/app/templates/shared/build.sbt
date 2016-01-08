organization := "<%= props.organization %>"

name := "<%= props.appName %>"

crossScalaVersions := Seq("2.10.6", "2.11.7")

val PlayVersion = "2.4.4"
val KafkaVersion_0_8 = "0.8.2.2"
val KafkaVersion_0_9 = "0.9.0.0"

lazy val root = project
  .in( file(".") )
  .aggregate(lib, playLib, kafkaLib_0_8)
  .settings(
    publish := {}
  )

lazy val lib = project
  .in(file("lib"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % PlayVersion
    )
  )

lazy val playLib = project
  .in(file("play-client"))
  .dependsOn(lib)
  .aggregate(lib)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-ws" % PlayVersion,
      "joda-time" % "joda-time" % "2.9.1"
    )
  )

lazy val kafkaLib_0_8 = project
  .in(file("kafka-lib_0_8"))
  .dependsOn(lib)
  .aggregate(lib)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % PlayVersion,
      "movio.api" %% "kafka-lib_0_8" % "0.1.0",
      "movio.core" %% "movio-core-utils" % "0.1.0",
      "mm" %% "testinglib" % "1.0.1" % "test",
      "mc" %% "kafka-testkit" % "1.3.3" % "test"
    )
  )

// lazy val kafkaLib_0_8 = TBC


lazy val commonSettings: Seq[Setting[_]] = Seq(
  name <<= name("<%= props.projectName %>-" + _),
  organization := "<%= props.organization %>",
  unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "main" / "generated",
  unmanagedSourceDirectories in Test += baseDirectory.value / "src" / "test" / "generated",
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "2.2.5" % "test"
  ),
  scalacOptions += "-feature",
  publishTo <<= version { (v: String) ⇒
    val repo = "https://artifactory.movio.co/artifactory/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("movio snapshots" at repo + "libs-snapshot-local")
    else
      Some("movio releases" at repo + "libs-release-local")
  }
)

releaseVersionBump := sbtrelease.Version.Bump.Minor
releaseTagName := s"${version.value}"
releaseCrossBuild := true

publishTo <<= version { (v: String) ⇒
  val repo = "https://artifactory.movio.co/artifactory/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("movio snapshots" at repo + "libs-snapshot-local")
  else
    Some("movio releases" at repo + "libs-release-local")
}
