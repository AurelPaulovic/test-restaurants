organization := "com.aurelpaulovic"

version := "0.1"

name := "restaurants"

scalaVersion := "2.12.6"

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-deprecation",
  "-language:implicitConversions",
  "-language:existentials",
  "-language:higherKinds",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint:-unused,_",
  "-Yno-adapted-args",
  "-Ypartial-unification",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-explaintypes",
  "-Ywarn-unused:patvars,-imports,-locals,-privates,-implicits",
  "-opt:l:inline",
  "-opt-inline-from:com.aurelpaulovic.**",
  "-opt-warnings"
)

scalacOptions in (Compile, doc) ++= Seq(
  "-no-link-warnings"
)

mainClass in (Compile, run) := Some("com.aurelpaulovic.restaurants.Server")

mainClass in (Compile, packageBin) := Some("com.aurelpaulovic.restaurants.Server")

assemblyMergeStrategy in assembly := {
  case PathList("org", "apache", "commons", "logging", xs @ _*) => MergeStrategy.first
  case PathList("org", "slf4j", xs @ _*) => MergeStrategy.first
  case "BUILD" => MergeStrategy.discard
  case "META-INF/io.netty.versions.properties" => MergeStrategy.last
  case other => MergeStrategy.defaultMergeStrategy(other)
}

assemblyJarName in assembly := "restaurants.jar"

resolvers ++= Seq(
  "Twitter maven" at "http://maven.twttr.com",
  "Finatra Repo" at "http://twitter.github.com/finatra"
)

lazy val depsLogging = Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
)

lazy val depsFunc = Seq(
  "org.typelevel" %% "cats-core" % "1.4.0"
)

lazy val depsServer = Seq(
  "com.twitter" %% "finatra-http" % "18.9.0"
)

lazy val depsConcurrent = Seq(

  "io.monix" %% "monix" % "3.0.0-RC1"
)

lazy val depsDb = Seq(
  "org.tpolecat" %% "doobie-core" % "0.5.3",
  "org.tpolecat" %% "doobie-postgres" % "0.5.3"
)

lazy val depsTests = Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "org.scalamock" %% "scalamock" % "4.1.0" % Test,
  "org.scalacheck" %% "scalacheck" % "1.14.0" % Test
)

lazy val depsDbTests = Seq(
  "org.tpolecat" %% "doobie-scalatest" % "0.5.3" % Test
)

lazy val depsServerTests = Seq(
  "com.twitter" %% "finatra-http" % "18.9.0" % Test classifier "tests",
  "com.twitter" %% "finatra-http" % "18.9.0" % Test,
  "com.twitter" %% "inject-core" % "18.9.0" % Test classifier "tests",
  "com.twitter" %% "inject-core" % "18.9.0" % Test,
  "com.twitter" %% "inject-modules" % "18.9.0" % Test classifier "tests",
  "com.twitter" %% "inject-modules" % "18.9.0" % Test,
  "com.twitter" %% "inject-app" % "18.9.0" % Test classifier "tests",
  "com.twitter" %% "inject-app" % "18.9.0" % Test,
  "com.twitter" %% "inject-server" % "18.9.0" % Test classifier "tests",
  "com.twitter" %% "inject-server" % "18.9.0" % Test,
  "com.google.inject" % "guice" % "4.0" % Test,
  "com.google.inject.extensions" % "guice-testlib" % "4.0" % Test,
  "org.mockito" % "mockito-core" %  "1.9.5" % Test,
  "com.twitter" %% "finatra-jackson" % "18.9.0" % Test classifier "tests",
  "com.twitter" %% "finatra-jackson" % "18.9.0" % Test
)

libraryDependencies ++= depsLogging ++ depsFunc ++ depsServer ++ depsConcurrent ++ depsDb ++ depsTests ++ depsDbTests ++ depsServerTests

test in assembly := {}

autoAPIMappings := true

cancelable in Global := true

lazy val restaurants = project.in(file("."))
