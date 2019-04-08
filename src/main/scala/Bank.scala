import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class Operation (
                      operation_id: Int,
                      date: String,
                      account_id: Int,
                      amount: Int,
                      balance: Int,
                      operation_type: String
                     )

case class Account (
                    account_id: Int,
                    balance: Int,
                    name: String
                   )

trait BankJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val operationFormat  : RootJsonFormat[Operation]  = jsonFormat6(Operation)
  implicit val accountFormat    : RootJsonFormat[Account]    = jsonFormat3(Account)

}