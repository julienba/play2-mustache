import sbt._
import Keys._
import PlayProject._
import org.jba.sbt.plugin.MustacheBuild

object ApplicationBuild extends Build with MustacheBuild{

    val appName         = "play2-mustachedemo"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      //"com.github.spullara.mustache.java" % "compiler" % "0.8.2"
      "org.jba" %% "play2-mustache" % "0.5.0",
      "com.twitter" %% "util-core" % "4.0.1" // For Twitter handler
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here
        
      resolvers += Resolver.url("julienba.github.com", url("http://julienba.github.com/repo/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("twitter", url("http://maven.twttr.com/"))(Resolver.ivyStylePatterns),
        
      //Import Mustache in all template
      //templatesImport += "org.jba.Mustache"
      
      // Mustache settings
      mustacheJSEntryPoints <<= (sourceDirectory in Compile)(base => base / "assets" / "mustache"),
      mustacheEntryPoints <<= (sourceDirectory in Compile)(base => base / "assets" ** "*.html"),

      mustacheOptions := Seq.empty[String],
      resourceGenerators in Compile <+= MustacheJSCompiler,
      resourceGenerators in Compile <+= MustacheFileCompiler
      
    )
}
