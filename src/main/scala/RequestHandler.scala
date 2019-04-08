object RequestHandler {
  val DEPOSIT = "deposit"
  val WITHDRAWAL = "withdrawal"

  def apply(database: Database): RequestHandler = new RequestHandler(database)
}

class RequestHandler(database: Database) {

  private def getUpdateBalance(operation: String, currentBalance: Int, amount: Int): Int = {
    operation match {
      case RequestHandler.DEPOSIT =>
        currentBalance + amount
      case RequestHandler.WITHDRAWAL =>
        currentBalance - amount
    }
  }

  def createAccount(name: String): MessageResponse = {
    if (name.length == 0 || name.exists(_.isDigit) || !name.exists(_.isLetter))
      throw new BadRequestException (
        code = 400,
        value = s"${Message.WRONG_PARAMETER} name, only letter character accepted"
      )
    database.insertAccountByName(name)
    MessageResponse (
      message = Message.ACCOUNT_CREATED,
      code = 201
    )
  }

  def makeOperation(operation: String, amount: Int, accountId: Int): MessageResponse = {
    val accountBalance = database.getAccountById(accountId)
    accountBalance map { account =>
      val updateBalance = getUpdateBalance(operation, account.balance, amount)
      if (updateBalance < 0)
        throw new BadRequestException (
          code = 400,
          value = Message.CANT_WITHDRAWAL
        )
      database.updateAccount(accountId, updateBalance)
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
    val operations = database.getOperationsByAccountId(accountId)
    if (operations.isEmpty)
      throw new NotFoundException (
        code = 404,
        value = Message.NO_OPERATION_OR_ACCOUNT_DOES_NOT_EXIST
      )
    operations
  }

  def getAccountBalance(accountId: Int): Option[Account] = {
    val account = database.getAccountById(accountId)
    if (account.isEmpty)
      throw new NotFoundException (
        code = 404,
        value = Message.ACCOUNT_DOES_NOT_EXIST
      )
    account
  }

}
