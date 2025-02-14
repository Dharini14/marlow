package services

import akka.http.scaladsl.client.RequestBuilding.Post
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import api.AccountRoutes
import com.marlow.bankingapp.models.Transaction
import kafka.TransactionProducer
import org.mockito.Mockito.when
import org.mockito.MockitoSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import repository.AccountRepository
import spray.json._

import scala.concurrent.Future

class AccountRoutesSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with MockitoSugar {

  val mockAccountRepo: AccountRepository = mock[AccountRepository]
  val mockAccountService: AccountService = mock[AccountService]
  val accountRoutes = new AccountRoutes(mockAccountRepo, mockAccountService)
  val mockKafkaProducer = TransactionProducer

  "AccountRoutes" should {

    "handle deposit request successfully" in {
      val accountId = "13579"
      val depositAmount = 200.0
      val requestJson = s"""{"accountId": $accountId, "amount": $depositAmount}""".parseJson

      // Mock service method
      when(mockAccountService.updateBalance(accountId, depositAmount)).thenReturn(Future.successful(Right(200.0)))

      //Test API route
      Post("/account/deposit", requestJson) ~> Route.seal(accountRoutes.route) ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe "Deposit successful"
      }

    }

    "handle withdrawal request successfully" in {
      val accountId = "13579"
      val withdrawAmount = -600.0
      val requestJson = s"""{"accountId": $accountId, "amount": $withdrawAmount}""".parseJson

      // Mock service failure
      when(mockAccountService.updateBalance(accountId, withdrawAmount))
        .thenReturn(Future.failed(new IllegalArgumentException("Insufficient funds")))


      //Test API route
      Post("/account/withdraw", requestJson) ~> Route.seal(accountRoutes.route) ~> check {
        status shouldBe StatusCodes.BadRequest
        responseAs[String] shouldBe "Insufficient funds"
      }
    }

    "Joint account withdrawals" should {
      "prevent overdraft when two users withdraw simultaneously" in {
        val accountId = "12345"
        val withdrawalAmount = BigDecimal(500)

        // Mock account balance before witdrawal
        when(mockAccountService.getBalance(accountId)).thenReturn(Future.successful(Right(1000.0)))

        // Mock service to allow only the first withdrawal
        when(mockAccountRepo.updateBalance(accountId.toLong, -withdrawalAmount))
          .thenReturn(Future.successful(500)) // First withdrawal succeeds
          .thenReturn(Future.successful(Left("Insufficient funds"))) // Second withdrawal fails

        val withdrawJson = Transaction(100040, accountId.toLong, -withdrawalAmount, "WITHDRAW")

        // First withdrawal succeeds
        Post("/account/withdraw").withEntity(HttpEntity(ContentTypes.`application/json`, withdrawJson)) ~> accountRoutes.route ~> check {
          status shouldBe StatusCodes.OK
          responseAs[String] should include("Withdrawal successfull")
        }

        // Second withdrawal fails
        Post("/account/withdraw").withEntity(HttpEntity(ContentTypes.`application/json`, withdrawJson)) ~> accountRoutes.route ~> check {
          status shouldBe StatusCodes.BadRequest
          responseAs[String] should include("insufficient funds")
        }

      }
    }

    "publish withdrawal event to kafka after a successful withdrawal" in {
      val accountId = "12345"
      val withdrawalAmount = BigDecimal(300)

      // Mock account balance
      when(mockAccountService.getBalance(accountId))
        .thenReturn(Future.successful(Right(1000.0))) // Initial balance = 1000.0

      when(mockAccountRepo.updateBalance(accountId.toLong, -withdrawalAmount))
        .thenReturn(Future.successful(300))

      val withdrawJson = Transaction(100023, accountId.toLong, -withdrawalAmount, "WITHDRAW").toJson.compactPrint

      Post("/acount/withdraw").withEntity(HttpEntity(ContentTypes.`application/json`, withdrawJson)) ~> accountRoutes.route ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] should include("Withdrawal successful")
      }

      // Verify Kafka event was published
      verify(mockKafkaProducer).sendTransaction(Transaction(100023, accountId.toLong, -withdrawalAmount, "WITHDRAW"))

    }
  }
}
