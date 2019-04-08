import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class MessageResponse (
                             code: Int,
                             message: String
                           )

case class ErrorMessage (
                         code : Int,
                         message : String
                       ) extends Exception


object Message {
  val ACCOUNT_CREATED = "your account has been created"
  val ACCOUNT_DOES_NOT_EXIST = "account doesn't exist"
  val CANT_WITHDRAWAL = "operation withdrawal failed, you can't overdraw your account"
  val OPERATION_SUCCEED = "operation succeed"
  val NO_OPERATION_OR_ACCOUNT_DOES_NOT_EXIST = "no operations or account doesn't exist"
  val WRONG_PARAMETER = "wrong parameter"
}

trait MessagesJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val messageResponseFormat : RootJsonFormat[MessageResponse] = jsonFormat2(MessageResponse)
  implicit val errorMessageFormat    : RootJsonFormat[ErrorMessage]    = jsonFormat2(ErrorMessage)
}