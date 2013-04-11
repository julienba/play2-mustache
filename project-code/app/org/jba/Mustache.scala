package org.jba

import com.github.mustachejava.DefaultMustacheFactory
import com.twitter.mustache.ScalaObjectHandler
import java.io.{StringWriter, InputStreamReader}

import org.apache.commons.lang.StringEscapeUtils
import scala.io.Source

import play.api._
import play.api.templates._
import play.api.Configuration._
import play.api.Play.current

class MustachePlugin(app: Application) extends Plugin {
  
  lazy val instance = {
    val i = new JavaMustache
    i
  }

  override def onStart(){
    Logger("mustache").info("start on mode: " + app.mode)
    instance 
  }
  
  def api: MustacheAPI = instance

  override lazy val enabled = {
    !app.configuration.getString("mustacheplugin").filter(_ == "disabled").isDefined
  } 
}

trait MustacheAPI {
  def render(template: String, data: Any): Html
}

class JavaMustache extends MustacheAPI{

  private lazy val fs = java.io.File.separator

  private val rootPath = fs + "public" + fs + "mustache" + fs
  
  val mf = createMustacheFactory

  private def createMustacheFactory = {
    val factory = new DefaultMustacheFactory {
      // override for load ressouce with play classloader
      override def getReader(resourceName: String): java.io.Reader  = {
        Logger("mustache").debug("read in factory: " + rootPath + resourceName + ".html")
        val input = Play.current.resourceAsStream(rootPath + resourceName  + ".html").getOrElse(throw new Exception("mustache: could not find template: " + resourceName))
        new InputStreamReader(input)
      }
    }
    factory.setObjectHandler(new ScalaObjectHandler)
    factory
  }

  private def readTemplate(template: String) = {
    Logger("mustache").debug("load template: " + rootPath + template)

    val factory = if(Play.isProd) mf else createMustacheFactory 

    val input = Play.current.resourceAsStream(rootPath + template + ".html").getOrElse(throw new Exception("mustache: could not find template: " + template))
    val mustache = factory.compile(new InputStreamReader(input), template)
    //factory.putTemplate(template, mustache)
    
    mustache    
  }
  
  def render(template: String, data: Any): Html = {
    Logger("mustache").debug("Mustache render template " + template)
        
    val mustache = {
      if(Play.isProd) {
        val maybeTemplate = mf.compile(template)
        if(maybeTemplate == null) {
          readTemplate(template)
        } else maybeTemplate
      } else {
        readTemplate(template)
      }
    }
    
    val writer = new StringWriter()
    mustache.execute(writer, data).flush()
    writer.close()    
    
    Html(writer.toString())    
  }
  
  private[jba] def loadAllTemplate: Unit = {
    Logger("mustache").info("Load all mustache template")
    
    Play.current.resource("mustache/mustache.tmpl").map { url =>
      for(fileName <- Source.fromFile(url.getFile()).getLines)
        readTemplate(fileName)    
    }.getOrElse {
      Logger("mustache").error("Impossible to read file mustache/mustache.tmpl")
    }
  }  
}

object Mustache {
  
  private def plugin = play.api.Play.maybeApplication.map { app =>
    app.plugin[MustachePlugin].getOrElse(throw new RuntimeException("you should enable MustachePlugin in play.plugins"))
  }.getOrElse(throw new RuntimeException("you should have a running app in scope a this point"))

  def render(template: String, data: Any): Html = plugin.api.render(template,data)

}
