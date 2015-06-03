name := """waffle"""

version := "0.1.0"

scalaVersion := "2.11.6"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "com.amazonaws" %  "aws-java-sdk" % "1.8.9.1",
  "joda-time"     %  "joda-time"    % "2.4",
  "org.specs2"    %% "specs2-core"  % "3.6.1" % "test",
  "org.specs2"    %% "specs2-mock"  % "3.6.1" % "test",
  "org.specs2"    %% "specs2-junit" % "3.6.1" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")
