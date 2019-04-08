import java.sql.Connection

import scala.collection.mutable.ListBuffer

class RequestHandler(db: Connection) {

  private val DEPOSIT = "deposit"
  private val WITHDRAWAL = "withdrawal"

  private def getUpdateBalance(operation: String, currentBalance: Int, amount: Int): Int = {
    operation match {
      case DEPOSIT =>
        currentBalance + amount
      case WITHDRAWAL =>
        currentBalance - amount
    }
  }
  def createAccount(name: String): MessageResponse = {
    val statement = db.prepareStatement(
      s"INSERT INTO account (name) VALUES ('$name');"
    )
    statement.execute()
    statement.close()
    MessageResponse (
      message = Message.ACCOUNT_CREATED,
      code = 201
    )
  }

  def makeOperation(operation: String, amount: Int, accountId: Int): MessageResponse = {
    val accountBalance = getAccountBalance(accountId)
    accountBalance map { account =>
      val updateBalance = getUpdateBalance(operation, account.balance, amount)
      if (updateBalance < 0)
        throw new BadRequestException (
          code = 400,
          value = Message.CANT_WITHDRAWAL
        )
      val statement = db.prepareStatement(
        s"UPDATE account SET balance = $updateBalance WHERE account_id = $accountId;"
      )
      statement.executeUpdate()
      statement.close()
      MessageResponse (
        message = Message.OPERATION_SUCCEED,
        code = 201
      )
    } getOrElse {
      throw new NotFoundException (
        code = 404,
        value = Message.ACCOUNT_DOES_NOT_EXIST
      )
    }
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
    if (operations.isEmpty)
      throw new NotFoundException (
        code = 404,
        value = Message.NO_OPERATION_OR_ACCOUNT_DOES_NOT_EXIST
      )
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
    if (account.isEmpty)
      throw new NotFoundException (
        code = 404,
        value = Message.ACCOUNT_DOES_NOT_EXIST
      )
    statement.close()
    account
  }

}
