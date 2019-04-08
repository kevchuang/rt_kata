import java.sql.{Connection, DriverManager}

import com.typesafe.config.ConfigFactory

import scala.collection.mutable.ListBuffer

object KataDatabase {
  private val config = ConfigFactory.load
  private val dbUrl = config.getString("db.url")
  private val dbUser = config.getString("db.user")
  private val dbPassword = config.getString("db.password")

  def apply(): KataDatabase = new KataDatabase(dbUrl, dbUser, dbPassword)
}

class KataDatabase(dbUrl: String, dbUser: String, dbPassword: String) extends Database {

  private var db: Connection = _
  Class.forName("org.postgresql.Driver")
  db = DriverManager.getConnection(dbUrl, dbUser, dbPassword)

  override def getAccountById(accountId: Int): Option[Account] = {
    val statement = db.createStatement()
    val result = statement.executeQuery(s"SELECT * FROM account WHERE account_id = $accountId")
    var account : Option[Account] = None
    while (result.next()) {
      val accountId = result.getInt("account_id")
      val balance = result.getInt("balance")
      val name = result.getString("name")
      account = Some(Account(accountId, balance, name))
    }
    statement.close()
    account
  }

  override def getOperationsByAccountId(accountId: Int): List[Operation] = {
    val statement = db.createStatement()
    val result = statement.executeQuery(s"SELECT * FROM operation WHERE account_id = $accountId")
    var operations = new ListBuffer[Operation]
    while (result.next()) {
      val operationId = result.getInt("operation_id")
      val date = result.getString("date")
      val accountId = result.getInt("account_id")
      val amount = result.getInt("amount")
      val balance = result.getInt("balance")
      val operationType = result.getString("operation_type")

      operations += Operation (operationId, date, accountId, amount, balance, operationType)
    }
    statement.close()
    operations.toList
  }

  override def updateAccount(accountId: Int, balance: Int): Int = {
    val statement = db.prepareStatement(
      s"UPDATE account SET balance = $balance WHERE account_id = $accountId;"
    )
    val result = statement.executeUpdate()
    statement.close()
    result
  }

  override def insertAccountByName(name: String): Boolean = {
    val statement = db.prepareStatement(
      s"INSERT INTO account (name) VALUES ('$name');"
    )
    val result = statement.execute()
    statement.close()
    result
  }
}

trait Database {
  def getAccountById(accountId: Int): Option[Account]
  def getOperationsByAccountId(accountId: Int): List[Operation]
  def updateAccount(accountId: Int, balance: Int): Int
  def insertAccountByName(name: String): Boolean
}