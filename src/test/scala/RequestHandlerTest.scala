import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec


class RequestHandlerTest extends FlatSpec with MockFactory {
  private val fakeDb = stub[Database]


  private def initFakeDb(): Unit = {
    fakeDb.getAccountById _ when 1 returns Some(Account(1, 30, "Kevin Durant"))
    fakeDb.getAccountById _ when 2 returns None
    fakeDb.getOperationsByAccountId _ when 1 returns List(Operation(1, "2019-04-05 06:50:23.557032", 1, 30, 30, "deposit"))
    fakeDb.getOperationsByAccountId _ when 2 returns List.empty
    fakeDb.insertAccountByName _ when "Kevin Durant" returns true
    fakeDb.updateAccount _ when(1, 30) returns 1
    fakeDb.updateAccount _ when(2, 30) returns 0
  }

  private val requestHandler = new RequestHandler(fakeDb)

  "RequestHander" should "create an account" in {
    initFakeDb()
    requestHandler.createAccount("Kevin Durant")
  }

  it should "throw BadRequestException on createAccount when there is a digit in the paramater" in {
    assertThrows[BadRequestException](requestHandler.createAccount("Kevin 123"))
  }

  it should "make an operation" in {
    initFakeDb()
    requestHandler.makeOperation("deposit", 30, 1)
  }

  it should "throw BadRequestException on makeOperation when the money in the balance isn't enough to withdraw" in {
    initFakeDb()
    assertThrows[BadRequestException](requestHandler.makeOperation("withdrawal", 40, 1))
  }

  it should "throw NotFoundException on makeOperation when the account doesn't exist" in {
    initFakeDb()
    assertThrows[NotFoundException](requestHandler.makeOperation("deposit", 30, 2))
  }

  it should "get operations" in {
    initFakeDb()
    requestHandler.getOperations(1)
  }

  it should "throw NotFoundException on getOperationsByAccountId when the account doesn't exist" in {
    initFakeDb()
    assertThrows[NotFoundException](requestHandler.getOperations(2))
  }

  it should "get account balance" in {
    initFakeDb()
    requestHandler.getAccountBalance(1)
  }

  it should "throw NotFoundException when the account doesn't exist" in {
    initFakeDb()
    assertThrows[NotFoundException](requestHandler.getAccountBalance(2))
  }

}
