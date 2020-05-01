name := "DungeonsAndTraining"

version := "0.1"

scalaVersion := "2.13.2"

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

libraryDependencies ++= Seq(
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "io.grpc" % "grpc-services" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "org.scalikejdbc" %% "scalikejdbc"       % "3.4.1",
  "org.postgresql" % "postgresql" % "42.2.12",
  "ch.qos.logback"  %  "logback-classic"   % "1.2.3",
  "com.zaxxer" % "HikariCP" % "1.3.+",
  "io.github.nremond" %% "pbkdf2-scala" % "0.6.5",
  "org.scalactic" %% "scalactic" % "3.1.1",
  "org.scalatest" %% "scalatest" % "3.1.1" % Test,
  "org.scalamock" %% "scalamock" % "4.4.0" % Test,
  "org.scalacheck" %% "scalacheck" % "1.14.1" % Test,
  "org.flywaydb" % "flyway-core" % "6.4.1" % Test
)

enablePlugins(PackPlugin)