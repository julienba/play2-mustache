package controllers

import play.api._
import play.api.mvc._
import models._


import play.api.libs.json._
import play.api.libs.json.Json._

object Application extends Controller{

  val contents = List(
	Content(1, "first"),
	Content(2, "second"),
	Content(3, "third")
  )
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def show = Action {
	val content = Content(1, "first")
    Ok(views.html.show(contents))
  }
  
  def json = Action {
    import models.ModelFormater.ContentFormat
   
    Ok(toJson(contents)) 
  }
}