import sbt.Defaults._

sbtPlugin := true

name := "play2-plugins-mustache"

version := "1.1.0"

organization := "org.jba"

addSbtPlugin("play" % "sbt-plugin" % "2.1-RC1")

publishMavenStyle := false

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Twitter Repository" at "http://maven.twttr.com/"

libraryDependencies += "com.github.spullara.mustache.java" % "compiler" % "0.8.2"

libraryDependencies += "com.twitter" % "util-core_2.9.1" % "4.0.1" // For Twitter handler

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

publishTo <<= (version) { version: String =>
  val localRepository = Resolver.file("local repo", new java.io.File(System.getProperty("user.home") + "/tmp/repo"))(Resolver.ivyStylePatterns)
  Some(localRepository)
}

