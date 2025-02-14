package services

import akka.http.scaladsl.client.RequestBuilding.Post
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import com.marlow.bankingapp.models.{Account, Transaction}
import kafka.TransactionProducer
import org.mockito.Mockito.{mock, when}
import org.scalatest.FailureMessages.should
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import repository.AccountRepository

import scala.concurrent.Future

class AccountServiceSpec extends AsyncWordSpec with Matchers {

  val mockAccountRepo = mock(classOf[AccountRepository])
  val accountService = new AccountService(mockAccountRepo)


  "AccountService" should {
    "create an account successfully" in {
     // Given: A valid account creation request
     val testAccount = Account(12345, "John D", 500.0)

      //Mock repository behavior
      when(mockAccountRepo.createAccount(testAccount)).thenReturn(Future.successful(1L))

      // When: Creating an account
      val result = accountService.createAccount(testAccount)

      // Then: The returned account ID should be 12345
      result.map { accountId =>
        accountId shouldBe 12345
      }
    }

    "fail to create an account with a negative balance" in {
      // Given: A valid account creation request
      val testAccount2 = Account(12323, "Lee Cooper", -1500.0)

      // When: Creating an account with a negative balance
      recoverToSucceededIf[IllegalArgumentException] {
        accountService.createAccount(testAccount2)
      }
    }


  }

}
