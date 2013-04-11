package org.jba.sbt.plugin

import sbt._
import sbt.NameFilter._
import Keys._
import play.Project._
import org.apache.commons.lang.StringEscapeUtils

object MustachePlugin extends sbt.Plugin {
  
  val mustacheEntryPoints = SettingKey[PathFinder]("play-mustache-entry-points")
  
  val mustacheOptions = SettingKey[Seq[String]]("play-mustache-options")
  
  val templates = scala.collection.mutable.Map.empty[String, String]
  
  lazy val mustacheTemplatesSettings: Seq[Project.Setting[_]] = Seq()
      
  val MustacheFileCompiler = MyAssetsCompiler(
    "mustache",
    (_ ** "*.html"),
    mustacheEntryPoints,   
    { (name, min) => name },
    { (mustacheFile, options) =>
      
      import scala.util.control.Exception._
      
      val jsSource = org.jba.sbt.plugin.MustacheCompiler.compile(mustacheFile, options)
      // Any error here would be because of Mustache, not the developer;
      // so we don't want compilation to fail.
//      val minified = catching(classOf[CompilationException])                                                                                                
//        .opt(play.core.jscompile.JavascriptCompiler.minify(jsSource, Some(mustacheFile.getName())))
      
      val minified = None
      (jsSource, minified, Seq(mustacheFile))
    },    
    mustacheOptions
  )
  
  // Name: name of the compiler
  // files: the function to find files to compile from the assets directory
  // naming: how to name the generated file from the original file and whether it should be minified or not
  // compile: compile the file and return the compiled sources, the minified source (if relevant) and the list of dependencies                                                                                                                                                                                               
  def MyAssetsCompiler(
    name: String,  
    watch: File => PathFinder, 
    filesSetting: sbt.SettingKey[PathFinder],
    naming: (String, Boolean) => String,
    compile: (File, Seq[String]) => (String, Option[String], Seq[File]),
    optionsSettings: sbt.SettingKey[Seq[String]]) =
    (sourceDirectory in Compile, resourceManaged in Compile, cacheDirectory, optionsSettings, filesSetting, classDirectory in Compile) map { (src, resources, cache, options, files, classDirectory) =>                                                                                                                                                                 

      import java.io._

      val cacheFile = cache / name    
      val currentInfos = watch(src).get.map(f => f -> FileInfo.lastModified(f)).toMap
      val (previousRelation, previousInfo) = Sync.readInfo(cacheFile)(FileInfo.lastModified.format)                                                                                                                                                                                                                          

      if (previousInfo != currentInfos) {                                                                                                                                                                                                                                                                                    

        // Delete previous generated files
        previousRelation._2s.foreach(IO.delete)                                                                                                                                                                                                                                                                              

        val generated = (files x relativeTo(Seq(src / "assets" ))).flatMap {                                                                                                                                                                                                                                                  
          case (sourceFile, name) => { 
            val (debug, min, dependencies) = compile(sourceFile, options) 
            val out = new File(resources, "public/" + naming(name, false))
            val outMin = new File(resources, "public/" + naming(name, true))                                                                                                                                                                                                                                                 
            IO.write(out, debug)
            
            // Add content in Map
            templates(name.replace(".html", "").replace("mustache/", "")) = debug
            
            // Write concatenate Javascript file
            val jsFile = classDirectory / "public/javascripts/mustache-tmpl.js"
            IO.write(jsFile, jsScript)            
            
            dependencies.map(_ -> out) ++ min.map { minified =>              
              dependencies.map(_ -> outMin)
            }.getOrElse(Nil)
          }
        }
        
        Sync.writeInfo(
            cacheFile,         
            Relation.empty[File, File] ++ generated,
            currentInfos
        )(FileInfo.lastModified.format)                                                                                                                                                                                                                                                                        

        generated.map(_._2).distinct.toList
      } else {

        // Return previously generated files
        previousRelation._2s.toSeq                                                                                                                                                                                                                                                                                           

      }

    }: sbt.Project.Initialize[sbt.Task[Seq[java.io.File]]]  
  
  /**
   * Generate something like that:
   *    var MUSTACHE_TEMPLATES = {
   *        "content" : "content",
   *"content/content2" : "content"
   *};
   */  
  def jsScript: String = {
    val output = new StringBuilder
    output ++= "var MUSTACHE_TEMPLATES={"
      
    // TODO: iterator...  
    for( (key, value) <- templates) {
      output ++= """'%s' : '%s',""".format(key, StringEscapeUtils.escapeJavaScript(value))      
    }
    
    // remove the last comma
    val outputAsString = output.toString
    val cleanOutput = 
      if(outputAsString.contains(","))
        outputAsString.substring(0, output.size - 1)
      else
        outputAsString
       
    cleanOutput + "};"    

  }
}

