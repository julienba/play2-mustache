import sbt.Defaults._

sbtPlugin := true

name := "play2-plugins-mustache"

version := "1.1.3"

organization := "org.jba"

scalacOptions += "-deprecation"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.0")

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

publishMavenStyle := false

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

publishMavenStyle := false

publishTo <<= (version) { version: String =>
  val localRepository = Resolver.file("local repo", new java.io.File(System.getProperty("user.home") + "/tmp/repo"))(Resolver.ivyStylePatterns)
  Some(localRepository)
}
