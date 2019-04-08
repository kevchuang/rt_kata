import akka.actor.ActorSystem

object Kata extends App {
//  val db = new Database
  val system = ActorSystem("monitor")
  val config = Configuration
  val port = config.webServer.port

  system.actorOf (WebServer.props(), "web-server")
}
