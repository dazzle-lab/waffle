name := """waffle"""

version := "0.1.0"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  "com.amazonaws" %  "aws-java-sdk" % "1.8.9.1",
  "joda-time"     %  "joda-time"    % "2.4",
  "org.specs2"    %% "specs2"       % "2.3.12" % "test",
  "org.mockito"   %  "mockito-core" % "1.9.5"  % "test"
)
