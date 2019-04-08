class NotFoundException(code : Int, value : String) extends Exception {
  def getCode = this.code
  def getValue = this.value
}

class ServerErrorException(code : Int, value : String) extends Exception {
  def getCode = this.code
  def getValue = this.value
}

class BadRequestException(code: Int, value: String) extends Exception {
  def getCode = this.code
  def getValue = this.value
}