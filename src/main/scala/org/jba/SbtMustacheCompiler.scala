package org.jba.sbt.plugin

import sbt._
import PlayProject._

import com.github.mustachejava._
import com.twitter.mustache._

import org.apache.commons.lang.StringEscapeUtils

import play.api._

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

}