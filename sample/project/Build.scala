import sbt._
import Keys._
import PlayProject._
//import org.jba.sbt.plugin.MustacheBuild
import org.jba.sbt.plugin.MustachePlugin
import org.jba.sbt.plugin.MustachePlugin._

object ApplicationBuild extends Build /*with MustacheBuild*/{

    val appName         = "play2-mustachedemo"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "org.jba" %% "play2-mustache" % "1.1.0",
      "com.twitter" % "util-core_2.9.1" % "4.0.1" // For Twitter handler
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA, settings = Defaults.defaultSettings /*++ mustacheTemplatesSettings*/).settings(
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
