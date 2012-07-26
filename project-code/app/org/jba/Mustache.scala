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
    
  override def onStart(){
    Logger("mustache").info("start on mode: " + app.mode)
    Mustache.loadAllTemplate
    Mustache.scriptValue = Mustache.jsTemplate
  }

  override lazy val enabled = {
    !app.configuration.getString("mustacheplugin").filter(_ == "disabled").isDefined
  } 
}

object Mustache {

  val rootPath = "app/views/mustache"
  private val mf = new DefaultMustacheFactory
  mf.setObjectHandler(new TwitterObjectHandler)
  var scriptValue: String = ""
  
  def loadAllTemplate = {
    Logger("mustache").info("Load all mustache template")
    var dir = new File(rootPath)
    
    for(file <- dir.listFiles.filter(file => file.isFile() && file.getName.endsWith(".html"))){
      Logger("mustache").debug("read: " + file.getName)  
      val template = Source.fromFile(new File(rootPath + "/" + file.getName), "UTF-8").mkString
      val mustache = mf.compile(new StringReader(template), "/" + file.getName)
      mf.putTemplate("/" + file.getName, mustache)
    }
  }
  
  def render(template: String, data: Any): Html = {
    Logger("mustache").debug("Mustache render template " + template)
    
    var mustache =
      if(Play.current.mode != Mode.Prod){
        val source = scala.io.Source.fromFile(new File(rootPath + "/" + template), "UTF-8").mkString
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
      
      if(file.isFile && file.getName.endsWith(".html")){
        val template = Source.fromFile(new File(rootPath + "/" + file.getName), "UTF-8").mkString
        
        scriptValue +=
          """
            "%s":"%s"
          """.format(StringEscapeUtils.escapeJavaScript("/" + file.getName), StringEscapeUtils.escapeJavaScript(template))
          
        if(it.hasNext) scriptValue += ","  
      }
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
