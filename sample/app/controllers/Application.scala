package controllers

import play.api._
import play.api.mvc._

import models._
import models.ModelFormater._

import play.api.libs.json._
import play.api.libs.json.Json._

import play.api.Play.current

object Application extends Controller{

  val user = User("alice@mail.com", "alice")
  val user2 = User("bob@mail.com", "bob")
  
  val users = List(user, user2)
  
  val contents = List(
	Content(1, "first", users),
	Content(2, "second", users),
	Content(3, "third", users)
  )

  def show = Action {
	val content = Content(1, "first", users)
    Ok(views.html.show(contents)) 
  }
  
  def json = Action {
    Ok(toJson(contents))
  }
}
