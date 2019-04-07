import com.typesafe.config.ConfigFactory

object Configuration {


  private lazy val root = ConfigFactory.load()

  lazy val webServer : WebServerConfig = {
    val config = root.getConfig("web-server")
    WebServerConfig (
      host = config.getString("host"),
      port = config.getInt("port")
    )
  }

  final case class WebServerConfig(
                                    host: String,
                                    port: Int
                                  )
}