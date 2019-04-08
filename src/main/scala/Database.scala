import java.sql.{Connection, DriverManager}

import com.typesafe.config.ConfigFactory

object Database {

  private val config = ConfigFactory.load
  private val dbUrl = config.getString("db.url")
  private val dbUser = config.getString("db.user")
  private val dbPassword = config.getString("db.password")

  def getConnection: Connection = {
    var db: Connection = null
    Class.forName("org.postgresql.Driver")
    db = DriverManager.getConnection(dbUrl, dbUser, dbPassword)
    db
  }

}
