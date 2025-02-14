package services

import com.marlow.bankingapp.models.Transaction
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.producer.KafkaProducer
import repository.{AccountRepository, TransactionRepository}
import slick.jdbc.MySQLProfile.api._
import kafka.TransactionProducer

import scala.concurrent.{ExecutionContext, Future}

class TransactionService(accountRepo: AccountRepository, transactionRepo: TransactionRepository)(implicit ec: ExecutionContext) extends LazyLogging {

  /** Deposit money into an account */
  def deposit(accountNumber: String, amount: BigDecimal): Future[Either[String, Transaction]] = {
    logger.info(s"Deposit Request: account=$accountNumber, amount=$amount")
    if (amount <= 0) Future.successful(Left("Deposit amount must be positive"))
    else {
      accountRepo.getAccount(accountNumber).flatMap {
        case Some(account) =>
          val newBalance = account.balance + amount
          val transaction = Transaction(0, account.id, amount, "DEPOSIT")

          for {
            _ <- accountRepo.updateBalance(account.id, newBalance)
            savedTxn <- transactionRepo.saveTransaction(transaction)
          } yield {
            TransactionProducer.sendTransaction(savedTxn)
            Right(savedTxn)
          }
        case None => Future.successful(Left("Account not found"))
      }
    }

  }

  /** Withdraw money with overdraft protection */
  def withdraw(accountNumber: String, amount: BigDecimal): Future[Either[String, Transaction]] = {
    logger.info(s"Withdraw Request: account=$accountNumber, amount=$amount")
  if (amount <= 0) Future.successful(Left("Withdrawal amount must be positive"))
    else {
      accountRepo.getAccount(accountNumber).flatMap {
        case Some(account) if account.balance >= amount =>
          val newBalance = account.balance - amount
          val transaction = Transaction(0, account.id, -amount, "WITHDRAWAL")

          for {
            _ <- accountRepo.updateBalance(account.id, newBalance)
            savedTxn <- transactionRepo.saveTransaction(transaction)
          } yield {
            TransactionProducer.sendTransaction(savedTxn)
            Right(savedTxn)
          }
        case Some(_) => Future.successful(Left("Insufficient balance"))
        case None => Future.successful(Left("Account not found"))
    }

  }
}

  /** Get transaction history */
  def getTransactionHistory(accountNumber: String) : Future[Either[String, Seq[Transaction]]] = {
  accountRepo.getAccount(accountNumber).flatMap {
    case Some(account) =>
      transactionRepo.getTransactionsByAccount(account.id).map(Right(_))
    case None => Future.successful(Left("Account not found"))
  }
  }
}