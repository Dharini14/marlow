package api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.Segment
import akka.http.scaladsl.server.Route
import com.marlow.bankingapp.models.Transaction
import services.TransactionService
import spray.json.DefaultJsonProtocol._

import scala.concurrent.ExecutionContext

class TransactionRoutes(transactionService: TransactionService) (implicit ec: ExecutionContext) {
  implicit val transactionFormat = jsonFormat4(Transaction)

  val route: Route =
    pathPrefix("transaction") {
      path("deposit" / Segment / DoubleNumber) { (accountNumber, amount) =>
        post {
          onSuccess(transactionService.deposit(accountNumber, BigDecimal(amount))) {
            case Right(transaction) => complete(transaction)
            case Left(errorMessage) => complete(StatusCodes.BadRequest, errorMessage)
          }
        }
      }~
      path("withdraw" / Segment / DoubleNumber) { (accountNumber, amount) =>
        post {
          onSuccess(transactionService.withdraw(accountNumber, BigDecimal(amount))) {
            case Right(transaction) => complete(transaction)
            case Left(errorMessage) => complete(StatusCodes.BadRequest, errorMessage)
            }
          }
        }~
      path("deposit" / Segment) { (accountNumber) =>
        post {
          onSuccess(transactionService.getTransactionHistory(accountNumber)) {
            case Right(transaction) => complete(transaction)
            case Left(errorMessage) => complete(StatusCodes.NotFound, errorMessage)
            }
          }
        }
    }

}
