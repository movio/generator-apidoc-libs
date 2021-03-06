organization := "<%= props.organization %>"

name := "<%= props.appName %>"

scalaVersion in ThisBuild := "2.11.8"

val PlayVersion = "2.4.4"
val SamzaVersion = "0.10.0"
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

lazy val samzaLib = project
  .in(file("samza-lib"))
  .dependsOn(lib)
  .dependsOn(testLib % Test)
  .aggregate(lib)
  .settings(commonSettings: _*)
  .settings(
    scalaVersion := "2.10.4",
    crossScalaVersions := Seq("2.10.4"),
    ivyScala := ivyScala.value map (_.copy(overrideScalaVersion = true)),
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % PlayVersion,
      "org.apache.samza" % "samza-api" % SamzaVersion,
      "joda-time" % "joda-time" % "2.2"
    )
  )

lazy val testLib = project
  .in(file("test-lib"))
  .dependsOn(lib)
  .aggregate(lib)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % PlayVersion,
      "joda-time" % "joda-time" % "2.2",
      "org.scalacheck" %% "scalacheck" % "1.12.5"
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

lazy val kafkaLib_0_9 = project
  .in(file("kafka-lib_0_9"))
  .dependsOn(lib)
  .dependsOn(testLib % Test)
  .aggregate(lib)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % PlayVersion,
      "org.apache.kafka" % "kafka-clients" % KafkaVersion_0_9,
      "joda-time" % "joda-time" % "2.9.1"
    )
  )

// ------------------------
// Release Settings
// ------------------------
// Release Process:
// Manual:
//   sbt
//   > release
//   > Release version [0.1.0] : ...
// Automatic:
//   sbt -Drelease_version=1.0.1 "release with-defaults"
releaseVersionBump := sbtrelease.Version.Bump.Minor
releaseTagName := version.value

val manualReleaseVersion = settingKey[String]("We're going to manage the version")
manualReleaseVersion := sys.props.get("release_version").getOrElse(
  if (releaseUseGlobalVersion.value) (version in ThisBuild).value else version.value
)

releaseTagName := manualReleaseVersion.value

releaseVersion := { ver =>
  sys.props.get("release_version").getOrElse(
    sbtrelease.Version(ver).map(_.withoutQualifier.string).getOrElse(sbtrelease.versionFormatError)
  )
}


lazy val commonSettings: Seq[Setting[_]] = Seq(
  name <<= name("<%= props.projectName %>-" + _),
  organization := "<%= props.organization %>",
  scalaVersion := "2.11.8",
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

publishTo <<= version { (v: String) ⇒
  val repo = "https://artifactory.movio.co/artifactory/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("movio snapshots" at repo + "libs-snapshot-local")
  else
    Some("movio releases" at repo + "libs-release-local")
}
