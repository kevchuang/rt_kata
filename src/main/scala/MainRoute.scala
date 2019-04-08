import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`, `Access-Control-Max-Age`}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, _}
import spray.json._

trait MainRoute extends MessagesJsonSupport with BankJsonSupport {

  private val requestHandler = new RequestHandler(KataDatabase())

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
          complete(HttpResponse(400, entity = HttpEntity(ContentTypes.`application/json`, ErrorMessage(400, s"malformation on request: ${e.getMessage}").toJson.compactPrint)))
        }
      case e: BadRequestException =>
        extractUri { _ =>
          complete(HttpResponse(400, entity = HttpEntity(ContentTypes.`application/json`, ErrorMessage(400, e.getValue).toJson.compactPrint)))
        }
      case e: MatchError =>
        extractUri { _ =>
          complete(HttpResponse(400, entity = HttpEntity(ContentTypes.`application/json`, ErrorMessage(400, "wrong value in parameters").toJson.compactPrint)))
        }
      case e: NotFoundException =>
        extractUri { _ =>
          complete(HttpResponse(404, entity = HttpEntity(ContentTypes.`application/json`, ErrorMessage(404, e.getValue).toJson.compactPrint)))
        }
      case e: ServerErrorException =>
        extractUri { _ =>
          complete(HttpResponse(500, entity = HttpEntity(ContentTypes.`application/json`, ErrorMessage(500, e.getValue).toJson.compactPrint)))
        }
    }
  
  val route: Route =
    respondWithHeaders(corsHeaders) {
      handleExceptions(mainExceptionHandler) {
        path("operations") {
            get {
              parameter('account_id) { accountId =>
                complete(requestHandler.getOperations(accountId.toInt))
              }
            } ~
            post {
              parameter('type, 'amount, 'account_id) { (operationType, amount, accountId) =>
                complete(requestHandler.makeOperation(operationType, amount.toInt, accountId.toInt))
              }
            }
          } ~ path("account") {
            get {
              parameter("id") { accountId =>
                complete(requestHandler.getAccountBalance(accountId.toInt))
              }
            } ~
            post {
              parameter('name) { name =>
                complete(requestHandler.createAccount(name))
              }
            }
        }
      }
    }


}
