package repository

import com.marlow.bankingapp.models.{Transaction, Transactions}
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}

class TransactionRepository(db: Database) (implicit ec: ExecutionContext) {

  private val transactions = TableQuery[Transactions]

  /** Save a new transaction (deposit or witdrawal) */
  def saveTransaction(transaction: Transaction): Future[Transaction] = {
    val insertQuery = transactions returning transactions.map(_.id) into ((txn, id) => txn.copy(id = id))
    db.run(insertQuery += transaction)
  }

  /** Get transaction history for an account */
  def getTransactionsByAccount(accountId: Long): Future[Seq[Transaction]] =
    db.run(transactions.filter(_.accountId === accountId).result)

}
