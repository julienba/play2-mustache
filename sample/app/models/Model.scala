package models

import play.api.libs.json._    
import play.api.libs.json.Json._
import play.api.libs.json.Generic._

case class Content(id: Long, name: String, users: List[User])
case class User(mail: String, name: String)

object ModelFormater {
  
  implicit val UserFormat: Format[User] = productFormat2("mail", "name")(User)(User.unapply)
  
  implicit object ContentFormat extends Format[Content] {
    def reads(json: JsValue):Content = Content(
      (json \ "id").as[Long],
      (json \ "name").as[String],
      (json \ "users").as[List[User]]
    )
    
    def writes(i: Content): JsValue = JsObject(Seq(
      "id" -> JsNumber(i.id),
      "name" -> JsString(i.name),
      "users" -> JsArray(i.users.map(UserFormat.writes(_)))
    ))
    
  }  
}