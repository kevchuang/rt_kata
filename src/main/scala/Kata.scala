import akka.actor.ActorSystem

object Kata extends App {

  val system = ActorSystem("kata")

  system.actorOf (WebServer.props(), "web-server")

}
