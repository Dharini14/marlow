package api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.marlow.bankingapp.models.{Account, Transaction}
import repository.AccountRepository
import services.AccountService
import spray.json.DefaultJsonProtocol._
import kafka.TransactionProducer

import scala.concurrent.ExecutionContext

class AccountRoutes (accountRepo: AccountRepository, accountService: AccountService)(implicit ec: ExecutionContext) {
  implicit val accountFormat = jsonFormat3(Account)
  implicit val transactionFormat = jsonFormat4(Transaction)

  val route: Route =
    pathPrefix("account") {
      path("create") {
        post {
          entity(as[Account]) { account =>
            onSuccess(accountService.createAccount(account)) {
              case Right(createdAccount) => complete(createdAccount)
              case Left(errorMessage) => complete(StatusCodes.BadRequest, errorMessage)
            }
          }
        }
      }~
      path("balance" / Segment) { accountNumber =>
        get {
          onSuccess(accountRepo.getAccount(accountNumber)) {
            case Some(account) => complete(account)
            case None => complete(StatusCodes.NotFound, "Account not found")
          }
        }
      }~
      path("deposit") {
        post {
          entity(as[Transaction]) { transaction =>
            if(transaction.transactionType != "DEPOSIT") {
              complete(StatusCodes.BadRequest, "Invalid transaction type")
            } else {
              onSuccess(accountRepo.getAccount(transaction.accountId.toString)) {
                case Some(account) =>
                  val newBalance = account.balance + transaction.amount
                  accountRepo.updateBalance(account.id, newBalance)
                  TransactionProducer.sendTransaction(transaction)
                  complete(s"Deposit successful, new balance: $newBalance")

                case None => complete(StatusCodes.NotFound, "Account not found")
              }
            }
          }
        }

      }
    }


}
