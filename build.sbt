import sbt.Defaults._

sbtPlugin := true

name := "play2-plugins-mustache"

version := "1.0.4"

organization := "org.jba"

addSbtPlugin("play" % "sbt-plugin" % "2.0.3")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

//libraryDependencies += "com.github.spullara.mustache.java" % "compiler" % "0.8.11"


//libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

publishMavenStyle := false

publishTo <<= (version) { version: String =>
  val localRepository = Resolver.file("local repo", new java.io.File(System.getProperty("user.home") + "/tmp/repo"))(Resolver.ivyStylePatterns)
  Some(localRepository)
}

