package repository

import com.marlow.bankingapp.models.{Account, Accounts}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class AccountRepository(db: Database) (implicit ec: ExecutionContext) {

  val accounts = TableQuery[Accounts]

  /** Insert a new account */
  def createAccount(account: Account): Future[Account] = {
    val insertQuery = accounts returning accounts.map(_.id) into ((acc, id) => acc.copy(id = id))
    db.run(insertQuery += account)
  }

  /** Fetch account details by account number */
  def getAccount(accountNumber: String): Future[Option[Account]] =
    db.run(accounts.filter(_.accountNumber === accountNumber).result.headOption)

  /** Update account balance */
  def updateBalance(accountId: Long, newBalance: BigDecimal): Future[Int] =
    db.run(accounts.filter(_.id === accountId).map(_.balance).update(newBalance))
}
