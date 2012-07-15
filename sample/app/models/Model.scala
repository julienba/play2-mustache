package models

import play.api.libs.json._    
import play.api.libs.json.Json._

case class Content(id: Long, name: String)

object ModelFormater {
  implicit object ContentFormat extends Format[Content] {
    def reads(json: JsValue):Content = Content(
      (json \ "id").as[Long],
      (json \ "name").as[String]
    )
    
    def writes(i: Content): JsValue = JsObject(Seq(
      "id" -> JsNumber(i.id),
      "name" -> JsString(i.name)
    ))
  }
}