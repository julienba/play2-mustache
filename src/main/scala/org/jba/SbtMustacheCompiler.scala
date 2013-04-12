package org.jba.sbt.plugin

import sbt._
import play.Project._

import play.api._

case class CompilationException(message: String, mustacheFile: File, atLine: Option[Int]) extends PlayException.ExceptionSource(
  "Compilation error", message) {
  def line = atLine.map(_.asInstanceOf[java.lang.Integer]).orNull
  def position = null
  def input = scalax.file.Path(mustacheFile).string
  def sourceName = mustacheFile.getAbsolutePath
}

object MustacheCompiler {

  import java.io._

  import org.mozilla.javascript._
  import org.mozilla.javascript.tools.shell._

  import scala.collection.JavaConverters._

  import scalax.file._
  import scalax.io._
  
  private lazy val compiler = {
    
    (source: File) => {
      val mustacheCode = Path(source).string.replace("\r", "")
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
