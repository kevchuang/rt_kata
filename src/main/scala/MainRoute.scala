import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`, `Access-Control-Max-Age`}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, _}
import spray.json._

trait MainRoute extends MessagesJsonSupport with BankJsonSupport {

  implicit val errorMessageJson = jsonFormat2(ErrorMessage)

  private val corsHeaders = List(
    `Access-Control-Allow-Headers`("request-header", "accept", "content-type"),
    `Access-Control-Allow-Methods`(POST, PUT, GET, DELETE, PATCH),
    `Access-Control-Allow-Origin`.`*`,
    `Access-Control-Max-Age`(600)
  )

  private val mainExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case e: NumberFormatException =>
        extractUri { _ =>
          complete(HttpResponse(400, entity = HttpEntity(ContentTypes.`application/json`, ErrorMessage("400", "malformation on request").toJson.compactPrint)))
        }
    }
  
  val route: Route =
    respondWithHeaders(corsHeaders) {
      handleExceptions(mainExceptionHandler) {
        path("operations") {
            get {
              parameter('account_id) { accountId =>
                complete(Database.getOperations(accountId.toInt))
              }
            } ~
            post {
              parameter('type, 'amount, 'account_id) { (operationType, amount, accountId) =>
                complete(Database.makeOperation(operationType, amount.toInt, accountId.toInt))
              }
            }
          } ~ path("account") {
            get {
              parameter("id") { accountId =>
                complete(Database.getAccountBalance(accountId.toInt))
              }
            }
        }
      }
    }


}
