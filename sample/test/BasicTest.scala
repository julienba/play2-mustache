package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import org.jba.Mustache

import models._

class BasicTestSpec extends Specification {
  
  case class Header(title: String)
  
  "Simple template" should {
    
    "compile" in new WithApplication {
      val htmlRes = Mustache.render("title", Header("Title"))
      htmlRes.toString.equals("<h3>Title</h3>")
    }
    
    "compile with map parameters" in new WithApplication {
      val params = Map('title -> "Title")
      val htmlRes = Mustache.render("title", params)
      htmlRes.toString.equals("<h3>Title</h3>")
    }
    
    "compile with option parameters" in new WithApplication {
      val params = Map('title -> Some("Title"))
      val htmlRes = Mustache.render("title", params)
      htmlRes.toString.equals("<h3>Title</h3>")
    }    
   
    "support .mustache file extension" in new WithApplication {
      val params = Map('title -> "Title")
      val html = Mustache.render("mustache_file_test", params)
      html.toString.equals("<h3>Title</h3>")
    }
    
    "play can load a mustache file" in new WithApplication {
      val Some(file) = app.resourceAsStream("/Users/michael/Projects/personal/play2-mustache/sample/app/assets/mustache/title.html")
    }

    "wrong template" in new WithApplication {
      Mustache.render("title-wrong", Header("Title")) must throwA[Exception]
    }
  }
  
  "Partial Template" should {
    "compile" in new WithApplication {
      
      val user = User("alice@mail.com", "alice")
      val user2 = User("bob@mail.com", "bob")
      val users = List(user, user2)
      val content = Content(1, "first", users)
      
      Mustache.render("content_partial_item", content).toString must contain("alice")
    }
  }  
}
