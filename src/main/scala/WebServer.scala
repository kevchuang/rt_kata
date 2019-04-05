import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object WebServer {

  def props(host: String, port: Int) : Props = Props (new WebServer(host, port))

}

class WebServer (host: String, port: Int) extends Actor with ActorLogging with MainRoute {

  implicit val system : ActorSystem = context.system
  implicit val materializer : ActorMaterializer = ActorMaterializer()
  implicit val ec : ExecutionContext = context.dispatcher

  private val binding: Future[ServerBinding] = Http().bindAndHandle(route, host, port)

  override def preStart(): Unit = {
    log.info(s"Kata web server starting ...")
    binding onComplete {
      case Success(ServerBinding(address)) =>
        log.info(s"Kata web server started at $address")

      case Failure(e) =>
        log.error(s"Kata web server failed to bind to $host:$port : {}", e)
    }
  }

  override def postStop(): Unit = {
    log.info(s"Kata web server stopping ...")
    binding flatMap (_.unbind) onComplete {
      case Success(_) =>
        log.info("Kata web server stopped")
      case Failure(e) =>
        log.error(s"Kata web server failed to unbind to $host:$port : {}", e)
    }
  }

  override def receive: Receive = {
    case _ =>
  }

}