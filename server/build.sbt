name := "DungeonsAndTraining"

version := "0.1"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra-http" % "20.4.1",
  "com.sun.activation" % "javax.activation" % "1.2.0",
  "io.netty" % "netty-transport-native-epoll" % "4.1.49.Final" classifier "linux-x86_64",
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core"   % "2.2.0",
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.2.0" % Provided,
  "org.scalikejdbc" %% "scalikejdbc"        % "3.4.2",
  "org.postgresql" % "postgresql" % "42.2.12",
  "ch.qos.logback"  %  "logback-classic"   % "1.2.3",
  "com.zaxxer" % "HikariCP" % "1.3.+",
  "io.github.nremond" %% "pbkdf2-scala" % "0.6.5",
  "joda-time" % "joda-time" % "2.10.6",
  "org.scalactic" %% "scalactic" % "3.1.1",
  "org.scalatest" %% "scalatest" % "3.1.1" % Test,
  "org.scalamock" %% "scalamock" % "4.4.0" % Test,
  "org.scalacheck" %% "scalacheck" % "1.14.1" % Test,
  "org.flywaydb" % "flyway-core" % "6.4.1" % Test,
  "com.softwaremill.sttp.client" %% "core" % "2.2.0",
  "com.github.dmarcous" % "s2utils_2.12" % "1.1.1",
  "com.lihaoyi" %% "pprint" % "0.5.6"
)

fork := true
enablePlugins(PackPlugin)