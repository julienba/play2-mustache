import sbt._
import Keys._

object MustacheBuild extends Build {
  val playPath = Option(System.getProperty("play.path")).getOrElse("../play")
  val playVersion = Option(System.getProperty("play.version")).getOrElse("2.0.2")

  val playRepository = Resolver.file("Local Play Repository", file(new File(playPath, "repository/local").getPath))(Resolver.ivyStylePatterns)

  val typesafeRepository = Resolver.url("Typesafe repository", url("http://repo.typesafe.com/typesafe/releases/"))(Resolver.ivyStylePatterns)

  val mvnRepository = Resolver.url("Standard repository", url("http://repo1.maven.org/maven2/"))(Resolver.ivyStylePatterns)

  lazy val root = Project(id = "mustache", base = file("."))
}
