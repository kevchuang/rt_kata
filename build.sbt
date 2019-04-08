name := "rt_kata"

version := "0.1"

scalaVersion := "2.12.8"

lazy val akkaHttpVersion = "10.1.8"
lazy val akkaVersion = "2.5.22"

lazy val akkaDependencies = Seq (
  "com.typesafe.akka" %% "akka-actor"           % akkaVersion,
  "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster"         % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools"   % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j"           % akkaVersion,
  "com.typesafe.akka" %% "akka-remote"          % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit"         % akkaVersion

)
lazy val akkaHttpDependencies = Seq (
  "com.typesafe.akka" %% "akka-http-core"       % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion
)

lazy val testDependencies = Seq (
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "org.scalamock" %% "scalamock" % "4.1.0" % Test
)

libraryDependencies ++= akkaHttpDependencies
libraryDependencies ++= akkaDependencies
libraryDependencies ++= testDependencies
libraryDependencies += "com.typesafe" % "config" % "1.3.2"
libraryDependencies += "org.postgresql" % "postgresql" % "42.1.1"
