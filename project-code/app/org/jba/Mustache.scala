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

class MustachePlugin(app: Application) extends Plugin {
    
  override def onStart(){
    if(app.mode == Mode.Prod){
      Mustache.loadAllTemplate
    }
    
    Nil
  }

  override lazy val enabled = {
    !app.configuration.getString("mustacheplugin").filter(_ == "disabled").isDefined
  } 
}

object Mustache {

  val rootPath = "app/views/mustache"
  private val mf = new DefaultMustacheFactory
  mf.setObjectHandler(new TwitterObjectHandler)
  private val scriptValue: String = jsTemplate
  
  def loadAllTemplate = {
    Logger("mustache").info("Load all mustache template")
    var dir = new File(rootPath)
    
    for(file <- dir.listFiles){
      val template = Source.fromFile(rootPath + "/" + file.getName).mkString
      val mustache = mf.compile(new StringReader(template), "/" + file.getName)
      mf.putTemplate("/" + file.getName, mustache)
    }
  }
  
  def render(template: String, data: Any): Html = {
    Logger("mustache").debug("Mustache render template " + template)
    
    var mustache =
      if(Play.current.mode != Mode.Prod){
        val source = scala.io.Source.fromFile(rootPath + "/" + template).mkString
        val mustache = mf.compile(new StringReader(source), "/" + template)
        mf.putTemplate("/" + template, mustache)
        mustache
      }else{  
      	mf.getTemplate("/" + template)
      }
    
	val writer = new StringWriter()
	mustache.execute(writer, data).flush()
	writer.close()    
    
    Html(writer.toString())    
  }

  def jsTemplate: String = {
    Logger("mustache").debug("Retrieve javascript template")
    var dir = new File(rootPath)
    
    var scriptValue = ""
    
    val it = dir.listFiles.iterator
    while(it.hasNext){
      val file = it.next
      val template = Source.fromFile(rootPath + "/" + file.getName).mkString

      scriptValue +=
        """
          "%s":"%s"
        """.format(StringEscapeUtils.escapeJavaScript("/" + file.getName), StringEscapeUtils.escapeJavaScript(template))
      
      if(it.hasNext) scriptValue += ","
    }
    
    """<script type="text/javascript">window.__MUSTACHE_TEMPLATES={""" + scriptValue + """}</script>""";
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
}
