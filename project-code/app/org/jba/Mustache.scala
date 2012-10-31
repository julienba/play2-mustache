package org.jba

import com.github.mustachejava._
import com.twitter.mustache._
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
    i.checkFiles
    i
  }

  override def onStart(){
    Logger("mustache").info("start on mode: " + app.mode)
    instance.checkFiles
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
  
  private val mf = new DefaultMustacheFactory
  mf.setObjectHandler(new TwitterObjectHandler)

  private[jba] def checkFiles: Unit = {
    if(!Play.getFile("app" + fs + "assets").exists())
      Logger.warn("app" + fs + "assets" + fs + "mustache directory is needed for mustache plugin")	
  }

  private def readTemplate(template: String) = {
    Logger("mustache").debug("load template: " + rootPath + template)
    val input = Play.current.resourceAsStream(rootPath + template + ".html").getOrElse(throw new Exception("mustache: could not find template: " + template))
    val mustache = mf.compile(new InputStreamReader(input), template)
    mf.putTemplate(template, mustache)
    mustache    
  }
  
  def render(template: String, data: Any): Html = {
    Logger("mustache").debug("Mustache render template " + template)
        
    var mustache = {
      
      if(Play.isProd) {
      
        val maybeTemplate = mf.getTemplate(template)
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
}

object Mustache {
  
  private def plugin = play.api.Play.maybeApplication.map{app =>
    app.plugin[MustachePlugin].getOrElse(throw new RuntimeException("you should enable MustachePlugin in play.plugins"))
  }.getOrElse(throw new RuntimeException("you should have a running app in scope a this point"))

  def render(template: String, data: Any): Html = plugin.api.render(template,data)

}
