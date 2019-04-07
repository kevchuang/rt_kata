import java.sql.{Connection, DriverManager}

import com.typesafe.config.ConfigFactory

import scala.collection.mutable.ListBuffer


object Database {

  private val config = ConfigFactory.load
  private val DEPOSIT = "deposit"
  private val WITHDRAWAL = "withdrawal"

  private var db: Connection = null
  Class.forName("org.postgresql.Driver")
  db = DriverManager.getConnection(config.getString("db.url"), config.getString("db.user"), config.getString("db.password"))

  private def getInsertAmount(operation: String, amount: Int) = {
    operation match {
      case DEPOSIT =>
        amount
      case WITHDRAWAL =>
        amount * (-1)
    }
  }

  private def getUpdateBalance(operation: String, amount: Int) = {
    operation match {
      case DEPOSIT =>
        s"account.balance + $amount"
      case WITHDRAWAL =>
        s"account.balance - $amount"
    }
  }

  def makeOperation(operation: String, amount: Int, accountId: Int): MessageResponse = {
    val insertAmount = getInsertAmount(operation, amount)
    val updateBalance = getUpdateBalance(operation, amount)
    val statement = db.prepareStatement(
      s"INSERT INTO account (account_id, balance) VALUES ($accountId, $insertAmount) ON CONFLICT (account_id) DO UPDATE SET balance = $updateBalance;"
    )
    statement.executeUpdate()
    statement.close()
    MessageResponse (
      message = "operation succeed",
      code = 200
    )
  }

  def getOperations(accountId: Int): List[Operation] = {
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

  def getAccountBalance(accountId: Int): Option[Account] = {
    val statement = db.createStatement()
    val result = statement.executeQuery(s"SELECT * FROM account WHERE account_id = $accountId")
    var account : Option[Account] = None
    while (result.next()) {
      val accountId = result.getInt("account_id")
      val balance = result.getInt("balance")

      account = Some(Account(accountId, balance))
    }
    statement.close()
    account
  }


}
