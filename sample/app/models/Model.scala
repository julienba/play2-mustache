package models

/**
 * Thanx to 
 * http://mandubian.com/
 */

// IMPORTANT import this to have the required tools in your scope
import play.api.libs.json._
// imports required functional generic structures
import play.api.libs.json.util._
// imports implicit structure for Writes only (always try to import only what you need)
import play.api.libs.json.Writes._

import play.api.data.validation.ValidationError
import play.api.libs.json.Reads._

case class Content(id: Long, name: String, users: List[User])
case class User(mail: String, name: String)

object ModelFormater {
  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val userWrites = Json.format[User]
  
  implicit val contentWrites = Json.format[Content]

}


