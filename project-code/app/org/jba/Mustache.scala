package org.jba

import com.github.mustachejava._
import com.twitter.mustache._
import java.io.{StringWriter, StringReader}
import java.io.File
import play.api.templates._
import org.apache.commons.lang.StringEscapeUtils
import scala.io.Source
import play.api._
import play.api.Configuration._
import java.io.InputStream

class MustachePlugin(app: Application) extends Plugin {
  
  lazy val instance = {
    val i = new JavaMustache
    i.loadAllTemplate
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

  def script(): Html 
}

class JavaMustache extends MustacheAPI{

  private lazy val fs = java.io.File.separator

  private val rootPath = Play.current.path + fs + "app" + fs + "views"+ fs + "mustache"

  private val mf = new DefaultMustacheFactory

  private val scriptValue = jsTemplate

  mf.setObjectHandler(new TwitterObjectHandler)

  private[this] def jsTemplate: String = {
    Logger("mustache").debug("Retrieve javascript template")
    var dir = new File(rootPath)
    
    val sValue = new StringBuffer
    
    val it = dir.listFiles.iterator
    while(it.hasNext){
      val file = it.next
      
      if(file.isFile && file.getName.endsWith(".html")){
        val template = Source.fromFile(new File(rootPath + fs  + file.getName), "UTF-8").mkString
        
        sValue.append(
          """
            "%s":"%s"
          """.format(StringEscapeUtils.escapeJavaScript(fs  + file.getName), StringEscapeUtils.escapeJavaScript(template))
         ) 
        if(it.hasNext) sValue.append(",")  
      }
    }
    
    """<script type="text/javascript">window.__MUSTACHE_TEMPLATES={""" + sValue.toString + """}</script>""";
  }
  
  private[jba] def loadAllTemplate: Unit = {
    Logger("mustache").info("Load all mustache template")
    var dir = new File(rootPath)
    
    for(file <- dir.listFiles.filter(file => file.isFile() && file.getName.endsWith(".html"))){
      Logger("mustache").debug("read: " + file.getName)  
      val template = Source.fromFile(new File(rootPath + fs  + file.getName), "UTF-8").mkString
      val mustache = mf.compile(new StringReader(template), fs  + file.getName)
      mf.putTemplate("/" + file.getName, mustache)
    }
  }

  /**
   * Write template as javascript object
   */
  def script(): Html = {
    Logger("mustache").info("Mustache render script")
    if(Play.current.mode == Mode.Prod){
      Html(scriptValue)
    }else{
      Html(jsTemplate)
    }
  }

  def render(template: String, data: Any): Html = {
    Logger("mustache").debug("Mustache render template " + template)
    
    var mustache =
      if(Play.current.mode != Mode.Prod){
        val source = scala.io.Source.fromFile(new File(rootPath + fs  + template), "UTF-8").mkString
        val mustache = mf.compile(new StringReader(source), fs  + template)
        mf.putTemplate(fs  + template, mustache)
        mustache
      }else{  
        mf.getTemplate(fs  + template)
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
  
  def script(): Html = plugin.api.script()

}
