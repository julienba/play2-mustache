import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "play2-mustachedemo"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      //"com.github.spullara.mustache.java" % "compiler" % "0.8.2"
        "org.jba" %% "play-mustache" % "0.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here
        watchSources <++= baseDirectory map { path => ((path / "app/views/mustache/") ** "*.html").get },
        //resolvers += Resolver.file("tmp-local", new File(Path.userHome.absolutePath + "/tmp/repo/"))(Resolver.ivyStylePatterns)
        resolvers += Resolver.url("julienba.github.com", url("http://julienba.github.com/repo/"))(Resolver.ivyStylePatterns) 

    )
}
