import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import api.{AccountRoutes, TransactionRoutes}
import config.DBConfig
import repository.{AccountRepository, TransactionRepository}
import services.{AccountService, TransactionService}

import scala.concurrent.ExecutionContextExecutor

object App {
  implicit val system: ActorSystem = ActorSystem("BankingApp")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val accountRepo = new AccountRepository(DBConfig.db)
  val accountService = new AccountService(accountRepo)
  val transactionRepo = new TransactionRepository(DBConfig.db)
  val transactionService = new TransactionService(accountRepo, transactionRepo)

  val accountRoutes = new AccountRoutes(accountRepo, accountService)
  val transactionRoutes = new TransactionRoutes(transactionService)


  def main(args: Array[String]): Unit = {
    Http().newServerAt("0.0.0.0", 8080).bind(accountRoutes.route)
    Http().newServerAt("0.0.0.0", 8081).bind(transactionRoutes.route)
    println("Server started at http://localhost:8080/")
  }



}
