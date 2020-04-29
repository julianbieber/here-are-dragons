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
  "ch.qos.logback"  %  "logback-classic"   % "1.2.3"
)
enablePlugins(PackPlugin)