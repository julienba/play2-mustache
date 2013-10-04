import sbt._
import Keys._
import play.Project._
import org.jba.sbt.plugin.MustachePlugin
import org.jba.sbt.plugin.MustachePlugin._

object ApplicationBuild extends Build {

    val appName         = "play2-mustache-demo"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "org.jba" %% "play2-mustache" % "1.1.3"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here
        
      resolvers += Resolver.url("julienba.github.com", url("http://julienba.github.com/repo/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("twitter", url("http://maven.twttr.com/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.file("local repo", new java.io.File(System.getProperty("user.home") + "/tmp/repo"))(Resolver.ivyStylePatterns),
        
      //Import Mustache in all template
      //templatesImport += "org.jba.Mustache"
      
      // Mustache settings
      mustacheEntryPoints <<= (sourceDirectory in Compile)(base => base / "assets" / "mustache" ** "*.html"),

      mustacheOptions := Seq.empty[String],
      resourceGenerators in Compile <+= MustacheFileCompiler
      
    )
}
