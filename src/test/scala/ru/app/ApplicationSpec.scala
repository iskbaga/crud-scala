import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.http4s.{Method, Request, Status, Uri}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import ru.app.controller.ServerEndpoints
import ru.app.error.BusinessApiError
import ru.app.model.domain._
import ru.app.service.ExpenseServiceImpl
import sttp.tapir.server.http4s.Http4sServerInterpreter
import tethys.JsonWriterOps
import tethys.jackson.jacksonTokenWriterProducer

class ApplicationSpec extends AnyWordSpec with Matchers {
  "ExpenseServiceImpl" should {
    val testStorage = List(
      Expense(
        UserName("John Doe"),
        ExpenseDescription("Groceries"),
        ExpenseAmount(50, 25),
        ExpenseCategory("Food"),
        ExpenseLocation("Local Supermarket"),
        ExpenseAccount("Credit Card"),
        Date("17.12.2022")
      ),
      Expense(
        UserName("Ivan Ivanov"),
        ExpenseDescription("Dinner"),
        ExpenseAmount(30, 75),
        ExpenseCategory("Dining"),
        ExpenseLocation("Restaurant"),
        ExpenseAccount("Cash"),
        Date("06.12.2022")
      ),
      Expense(
        UserName("John Ivanov"),
        ExpenseDescription("Gasoline"),
        ExpenseAmount(40, 50),
        ExpenseCategory("Transportation"),
        ExpenseLocation("Gas Station"),
        ExpenseAccount("Credit Card"),
        Date("19.11.2022")
      ),
      Expense(
        UserName("Sergey"),
        ExpenseDescription("Electronics"),
        ExpenseAmount(120, 0),
        ExpenseCategory("Shopping"),
        ExpenseLocation("Tech Store"),
        ExpenseAccount("Online Wallet"),
        Date("06.07.2022")
      ),
      Expense(
        UserName("Ruslan"),
        ExpenseDescription("Movie Night"),
        ExpenseAmount(20, 50),
        ExpenseCategory("Entertainment"),
        ExpenseLocation("Cinema"),
        ExpenseAccount("Cash"),
        Date("06.07.2022")
      ),
      Expense(
        UserName("Danil"),
        ExpenseDescription("Movie Night"),
        ExpenseAmount(20, 50),
        ExpenseCategory("Entertainment"),
        ExpenseLocation("Cinema"),
        ExpenseAccount("Cash"),
        Date("06.07.2022")
      )
    )
    val expenseService = ExpenseServiceImpl.stub[IO]()
    "return all expenses" in {
      val result = expenseService.getAllExpenses.unsafeRunSync()

      result shouldEqual Right(testStorage)
    }
    "delete expense" in {
      val result = expenseService.deleteExpense(1).unsafeRunSync()

      result shouldEqual Right("b")
    }
    "return expense by correct ID" in {
      val result = expenseService.getExpenseById(1).unsafeRunSync()
      result shouldEqual Right(testStorage.head)
    }
    "error expense by not correct ID" in {
      val result = expenseService.getExpenseById(7).unsafeRunSync()
      result shouldEqual Left(BusinessApiError("По данному запросу ничего не найдено"))
    }
    "update expense" in {
      val updatedExpense = Expense(
        UserName("Sergey"),
        ExpenseDescription("Electronics"),
        ExpenseAmount(600, 33),
        ExpenseCategory("Shopping"),
        ExpenseLocation("Tech Store"),
        ExpenseAccount("Online Wallet"),
        Date("06.07.2022")
      )
      val result = expenseService.updateExpense(4, updatedExpense).unsafeRunSync()
      result shouldEqual Right("a")
    }

    "handle error during expense deletion" in {
      val result = expenseService.deleteExpense(13).unsafeRunSync()
      result shouldEqual Right("b")
    }
    "getTotalAmountByName correctly" in {
      val result = expenseService.getTotalAmountByName("Ruslan").unsafeRunSync()
      result shouldEqual Right("20.5")
    }
    "getTotalAmountByCategory correctly" in {
      val result = expenseService.getTotalAmountByCategory("Entertainment").unsafeRunSync()
      result shouldEqual Right("41.0")
    }

    "getTotalAmountByLocation correctly" in {
      val result = expenseService.getTotalAmountByLocation("Cinema").unsafeRunSync()
      result shouldEqual Right("41.0")
    }
    "getTotalAmountByAccount correctly" in {
      val result = expenseService.getTotalAmountByAccount("Cash").unsafeRunSync()
      result shouldEqual Right("71.75")
    }

    "getExpensesByName correctly" in {
      val result = expenseService.getExpensesByName("Danil").unsafeRunSync()
      result shouldEqual Right(List(testStorage(5)))
    }

    "getExpensesByAccount correctly" in {
      val result = expenseService.getExpensesByAccount("Credit Card").unsafeRunSync()
      result shouldEqual Right(List(testStorage.head, testStorage(2)))
    }

    "getExpensesByLocation correctly" in {
      val result = expenseService.getExpensesByLocation("Tech Store").unsafeRunSync()
      result shouldEqual Right(List(testStorage(3)))
    }

    "getExpensesByCategory correctly" in {
      val result = expenseService.getExpensesByCategory("Entertainment").unsafeRunSync()
      result shouldEqual Right(List(testStorage(4), testStorage(5)))
    }
  }
  "ServerEndpoints" should {
    val expense = Expense(
      UserName("John Doe"),
      ExpenseDescription("Groceries"),
      ExpenseAmount(50, 25),
      ExpenseCategory("Food"),
      ExpenseLocation("Local Supermarket"),
      ExpenseAccount("Credit Card"),
      Date("17.12.2022")
    )

    val serverEndpoints = ServerEndpoints.stub[IO]()

    "have the correct number of API endpoints" in {
      serverEndpoints.apiEndpoints.length shouldEqual 14
    }

    "helloServerEndpoint" in {
      val routes   = Http4sServerInterpreter[IO]().toRoutes(serverEndpoints.helloServerEndpoint)
      val request  = Request[IO](uri = Uri.uri("hello").withQueryParam("Expense", expense.asJson))
      val response = routes.orNotFound.run(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      response.as[String].unsafeRunSync() shouldBe s"Hello ${expense.userName.name}"
    }

    "deleteExpenseServerEndpoint" in {

      val routes = Http4sServerInterpreter[IO]().toRoutes(serverEndpoints.deleteExpenseServerEndpoint)
      val request = Request[IO](uri = Uri.uri("/expenses/1"))
        .withMethod(Method.DELETE)

      val response = routes.orNotFound.run(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      response.as[String].unsafeRunSync() shouldBe "b"
    }

    "updateExpenseServerEndpoint" in {
      val routes = Http4sServerInterpreter[IO]().toRoutes(serverEndpoints.updateExpenseServerEndpoint)
      val request = Request[IO](uri = Uri.uri("/expenses/1"))
        .withMethod(Method.PUT)
        .withEntity(expense.asJson)

      val response = routes.orNotFound.run(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      response.as[String].unsafeRunSync() shouldBe "a"
    }

    "addExpenseServerEndpoint" in {
      val routes = Http4sServerInterpreter[IO]().toRoutes(serverEndpoints.addExpenseServerEndpoint)
      val request = Request[IO](uri = Uri.uri("/expenses/add"))
        .withMethod(Method.POST)
        .withEntity(expense.asJson)

      val response = routes.orNotFound.run(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      response.as[String].unsafeRunSync() shouldBe "42"
    }

    "getLocationServerEndpoint" in {
      val routes  = Http4sServerInterpreter[IO]().toRoutes(serverEndpoints.getLocationServerEndpoint)
      val request = Request[IO](uri = Uri.uri("/expenses/filter/Cinema"))

      val response = routes.orNotFound.run(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      response.as[String].unsafeRunSync() shouldBe """[{"userName":{"name":"Ruslan"},"description":{"value":"Movie Night"},"amount":{"integerPart":20,"fractionalPart":50},"category":{"value":"Entertainment"},"location":{"value":"Cinema"},"account":{"value":"Cash"},"date":{"date":"06.07.2022"}},{"userName":{"name":"Danil"},"description":{"value":"Movie Night"},"amount":{"integerPart":20,"fractionalPart":50},"category":{"value":"Entertainment"},"location":{"value":"Cinema"},"account":{"value":"Cash"},"date":{"date":"06.07.2022"}}]"""
    }

    "getCategoryServerEndpoint" in {
      val routes  = Http4sServerInterpreter[IO]().toRoutes(serverEndpoints.getCategoryServerEndpoint)
      val request = Request[IO](uri = Uri.uri("/expenses/filter/Food"))

      val response = routes.orNotFound.run(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      response.as[String].unsafeRunSync() shouldBe """[{"userName":{"name":"John Doe"},"description":{"value":"Groceries"},"amount":{"integerPart":50,"fractionalPart":25},"category":{"value":"Food"},"location":{"value":"Local Supermarket"},"account":{"value":"Credit Card"},"date":{"date":"17.12.2022"}}]"""
    }

    "getAccountServerEndpoint" in {
      val routes  = Http4sServerInterpreter[IO]().toRoutes(serverEndpoints.getAccountServerEndpoint)
      val request = Request[IO](uri = Uri.uri("/expenses/filter/Debit%20Card"))

      val response = routes.orNotFound.run(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      response.as[String].unsafeRunSync() shouldBe "[]"
    }

    "getUserServerEndpoint" in {
      val routes  = Http4sServerInterpreter[IO]().toRoutes(serverEndpoints.getUserServerEndpoint)
      val request = Request[IO](uri = Uri.uri("/expenses/filter/John%20Doe"))

      val response = routes.orNotFound.run(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      response.as[String].unsafeRunSync() shouldBe """[{"userName":{"name":"John Doe"},"description":{"value":"Groceries"},"amount":{"integerPart":50,"fractionalPart":25},"category":{"value":"Food"},"location":{"value":"Local Supermarket"},"account":{"value":"Credit Card"},"date":{"date":"17.12.2022"}}]"""
    }

    "getAmountByLocationServerEndpoint" in {
      val routes  = Http4sServerInterpreter[IO]().toRoutes(serverEndpoints.getAmountByLocationServerEndpoint)
      val request = Request[IO](uri = Uri.uri("/expenses/total/location/Tech%20Store"))

      val response = routes.orNotFound.run(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      response.as[String].unsafeRunSync() shouldBe "120"
    }

    "getAmountByAccountServerEndpoint" in {
      val routes  = Http4sServerInterpreter[IO]().toRoutes(serverEndpoints.getAmountByAccountServerEndpoint)
      val request = Request[IO](uri = Uri.uri("/expenses/total/account/Cash"))

      val response = routes.orNotFound.run(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      response.as[String].unsafeRunSync() shouldBe "71.75"
    }

    "getAmountByUserServerEndpoint" in {
      val routes  = Http4sServerInterpreter[IO]().toRoutes(serverEndpoints.getAmountByUserServerEndpoint)
      val request = Request[IO](uri = Uri.uri("/expenses/total/name/John%20Doe"))

      val response = routes.orNotFound.run(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      response.as[String].unsafeRunSync() shouldBe "50.25"
    }

    "getAmountByCategoryServerEndpoint" in {
      val routes  = Http4sServerInterpreter[IO]().toRoutes(serverEndpoints.getAmountByCategoryServerEndpoint)
      val request = Request[IO](uri = Uri.uri("/expenses/total/category/Food"))

      val response = routes.orNotFound.run(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      response.as[String].unsafeRunSync() shouldBe "50.25"
    }
  }
}
