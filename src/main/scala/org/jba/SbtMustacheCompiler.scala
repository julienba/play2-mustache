package org.jba.sbt.plugin

import sbt._
import PlayProject._

import com.github.mustachejava._
import com.twitter.mustache._

import org.apache.commons.lang.StringEscapeUtils

import play.api._
import play.api.Logger

trait MustacheBuild {

  val mustacheJSEntryPoints = SettingKey[PathFinder]("play-mustache-js-entry-points")
  val mustacheEntryPoints = SettingKey[PathFinder]("play-mustache-entry-points")
  val mustacheOptions = SettingKey[Seq[String]]("play-mustache-options")
  
  val MustacheJSCompiler = AssetsCompiler(
    "mustache",
    (_ ** "*.html"),
    mustacheJSEntryPoints,   
    { (name, min) => "mustache_template.js" },
    { (mustacheDir, options) =>
      
      import scala.util.control.Exception._
      Logger("mustache.build").debug("Compile one mustache dir : " + mustacheDir.getAbsoluteFile())
      
      val (jsSource, dependencies) = org.jba.sbt.plugin.MustacheCompiler.compileDir(mustacheDir, options)
      println("Compile JS dir:" + mustacheDir.getPath())
      
      // Any error here would be because of Mustache, not the developer;
      // so we don't want compilation to fail.
      val minified = catching(classOf[CompilationException])                                                                                                
        .opt(play.core.jscompile.JavascriptCompiler.minify(jsSource, Some(mustacheDir.getName())))

        (jsSource, minified, dependencies)
    },    
    mustacheOptions
  )
  
  val MustacheFileCompiler = AssetsCompiler(
    "mustache",
    (_ ** "*.html"),
    mustacheEntryPoints,   
    { (name, min) => name },
    { (mustacheFile, options) =>
      
      import scala.util.control.Exception._
      Logger("mustache.build").debug("Compile one mustache file: " + mustacheFile.getAbsoluteFile())

      val jsSource = org.jba.sbt.plugin.MustacheCompiler.compile(mustacheFile, options)
      println("Compile file:" + mustacheFile.getPath())
      // Any error here would be because of Mustache, not the developer;
      // so we don't want compilation to fail.
//      val minified = catching(classOf[CompilationException])                                                                                                
//        .opt(play.core.jscompile.JavascriptCompiler.minify(jsSource, Some(mustacheFile.getName())))
      
      val minified = None
      (jsSource, minified, Seq(mustacheFile))
    },    
    mustacheOptions
  )  
  
}

case class CompilationException(message: String, mustacheFile: File, atLine: Option[Int]) extends PlayException(
  "Compilation error", message) with PlayException.ExceptionSource {
  def line = atLine
  def position = None
  def input = Some(scalax.file.Path(mustacheFile))
  def sourceName = Some(mustacheFile.getAbsolutePath)
}

object MustacheCompiler {

  import java.io._

  import org.mozilla.javascript._
  import org.mozilla.javascript.tools.shell._

  import scala.collection.JavaConverters._

  import scalax.file._
  
  private lazy val compiler = {
    
    (source: File) => {
      val mustacheCode = Path(source).slurpString.replace("\r", "")
      mustacheCode
    }
    
  }
  
  def compile(source: File, options: Seq[String]): String = {
    try {
      Logger("mustache.build").debug("mustache: compile file %s".format(source.getPath()))
      compiler(source)
    } catch {
      case e: JavaScriptException => {

        val line = """.*on line ([0-9]+).*""".r
        val error = e.getValue.asInstanceOf[Scriptable]

        throw ScriptableObject.getProperty(error, "message").asInstanceOf[String] match {
          case msg @ line(l) => CompilationException(
            msg,
            source,
            Some(Integer.parseInt(l)))
          case msg => CompilationException(
            msg,
            source,
            None)
        }

      }
    }
  }
  
  def compileDir(root: File, options: Seq[String]): (String, Seq[File]) = {
    
    val dependencies = Seq.newBuilder[File]
    val output = new StringBuilder
    /**
     * Generate something like that:
     * 	var MUSTACHE_TEMPLATES = {
     * 		"content" : "content",
     *		"content/content2" : "content"
	 *	};
     */
    output ++= "var MUSTACHE_TEMPLATES={"
      
    def addTemplateDir(dir: File, path: String) {
      for {
        file <- dir.listFiles.toSeq.sortBy(_.getName)
        name = file.getName
      } {
        if (file.isDirectory) {
          addTemplateDir(file, path + name + "/")
        }
        else if (file.isFile && name.endsWith(".html")) {
          val templateName = path + name.replace(".html", "")
          Logger("mustache.build").debug("mustache: processing template %s".format(templateName))
          val jsSource = compile(file, options)
          dependencies += file
          output ++= """'%s' : '%s',"""
            .format(templateName, StringEscapeUtils.escapeJavaScript(jsSource))
        }
      }
    }
    
    addTemplateDir(root, "") 
    
    // remove the last comma
    val outputAsString = output.toString
    val cleanOutput = 
      if(outputAsString.contains(","))
        outputAsString.substring(0, output.size - 1)
      else
        outputAsString
       
    (cleanOutput + "};", dependencies.result)
  }

}