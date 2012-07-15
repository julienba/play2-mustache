sbtPlugin := true

name := "play-mustache"

organization := "org.jba"

version := "0.1"

resolvers += playRepository

//scalaVersion := "2.9.1"

//resolvers += mvnRepository

resolvers += typesafeRepository

libraryDependencies += "com.github.spullara.mustache.java" % "compiler" % "0.8.2"

addSbtPlugin("play" % "sbt-plugin" % playVersion)

//publishTo := Some(playRepository)

publishTo <<= (version) { version: String =>
  val localReleases = Resolver.file("local realease", Path.userHome / "tmp" / "repo" asFile)(Resolver.ivyStylePatterns) 
  Some(localReleases)
}

publishMavenStyle := false
