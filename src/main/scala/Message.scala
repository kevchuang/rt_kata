import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class MessageResponse (
                             message: String,
                             code: Int
                           )

case class ErrorMessage(code : String, value : String)

trait MessagesJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val messageResponseFormat : RootJsonFormat[MessageResponse] = jsonFormat2(MessageResponse)
  implicit val errorMessageFormat    : RootJsonFormat[ErrorMessage]    = jsonFormat2(ErrorMessage)

}