package services

import com.marlow.bankingapp.models.{Account, Transaction}
import com.typesafe.scalalogging.LazyLogging
import repository.AccountRepository

import scala.concurrent.{ExecutionContext, Future}

class AccountService (accountRepo: AccountRepository)(implicit ec: ExecutionContext) extends LazyLogging {

  /** Create a new account */
  def createAccount(account: Account): Future[Either[String, Account]] = {
    logger.info(s"Create Request: account=$account")
    accountRepo.getAccount(account.accountNumber).flatMap {
      case Some(_) => Future.successful(Left("Account already exists"))
      case None =>
        accountRepo.createAccount(account).map(Right(_))
    }
  }

  /** Get account details by accountnumber */
  def getAccount(accountNumber: String): Future[Either[String, Account]] = {
    accountRepo.getAccount(accountNumber).map {
      case Some(account) => Right(account)
      case None => Left("Account not found")
    }
  }

  /** Check balance */
  def getBalance(accountNumber: String): Future[Either[String, BigDecimal]] = {
    accountRepo.getAccount(accountNumber).map {
      case Some(account) => Right(account.balance)
      case None => Left("Account not found")
    }
  }

  /** Update balance (used for deposits/withdrawals) */
  def updateBalance(accountNumber: String, amount: BigDecimal): Future[Either[String, BigDecimal]] = {
    accountRepo.getAccount(accountNumber).flatMap {
      case Some(account) =>
        val newBalance = account.balance + amount
        if (newBalance < 0) Future.successful(Left("Insufficient balance"))
        else {
          accountRepo.updateBalance(account.id, newBalance).map(_ => Right(newBalance))
        }
      case None => Future.successful(Left("Account not found"))
    }
  }


}
