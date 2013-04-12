import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play2-mustache"

  val appVersion      = "1.1.2"

  val appDependencies = Seq(
    "com.github.spullara.mustache.java" % "compiler" % "0.8.11",
    "commons-lang" % "commons-lang" % "2.6"
  )

  object Resolvers {
    // publish to my local github website clone, I will push manually 
    val localRepository = Resolver.file("local repo", new java.io.File(System.getProperty("user.home") + "/tmp/repo"))(Resolver.ivyStylePatterns)
  }

  val main = play.Project(appName, appVersion, appDependencies).settings(
    organization := "org.jba",
    publishMavenStyle := false,
    publishTo := Some(Resolvers.localRepository),
    resolvers += Resolver.url("twitter", url("http://maven.twttr.com/"))(Resolver.ivyStylePatterns)
  )
}
