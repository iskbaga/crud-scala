package ru.app.controller.endpoint
import ru.app.error.{ApiError, BusinessApiError, ServerApiError}
import ru.app.model.domain._
import sttp.model.StatusCode
import sttp.tapir.json.tethysjson.jsonBody

object ExpenseEndpoints {
  import sttp.tapir._

  val helloEndpoint: PublicEndpoint[Expense, Unit, String, Any] =
    endpoint
      .get
      .in("hello")
      .in(query[Expense]("Expense").example(Expense.example))
      .out(stringBody)

  private val apiEndpoint: PublicEndpoint[Unit, ApiError, Unit, Any] =
    endpoint
      .errorOut(oneOf[ApiError](
        oneOfVariant(StatusCode.InternalServerError, jsonBody[ServerApiError]),
        oneOfVariant(StatusCode.UnprocessableEntity, jsonBody[BusinessApiError])
      ))
  private val userPath =
    apiEndpoint
      .in("expenses" / path[Int]("id"))

  val allExpenses: PublicEndpoint[Unit, ApiError, List[Expense], Any] =
    apiEndpoint.get
      .in("expenses" / "list" / "all")
      .out(jsonBody[List[Expense]].example(
        List(
          Expense(
            UserName("Ivan"),
            ExpenseDescription("Payment for the dormitory"),
            ExpenseAmount(2200, 33),
            ExpenseCategory("House"),
            ExpenseLocation("Vyazemskiy per"),
            ExpenseAccount("Credit card"),
            Date("11.12.2023")
          ),
          Expense(
            UserName("Bob"),
            ExpenseDescription("Gasoline"),
            ExpenseAmount(123, 33),
            ExpenseCategory("Transportation"),
            ExpenseLocation("Gas Station"),
            ExpenseAccount("Credit Card"),
            Date("06.07.2004")
          )
        )
      ))

  val expenseById: PublicEndpoint[Int, ApiError, Expense, Any] =
    userPath
      .get
      .out(jsonBody[Expense])
  val addExpenses: PublicEndpoint[Expense, ApiError, String, Any] =
    apiEndpoint
      .post
      .in("expenses" / "add")
      .in(jsonBody[Expense].example(Expense.example))
      .out(stringBody)

  val updateExpense: PublicEndpoint[(Int, Expense), ApiError, String, Any] =
    userPath
      .put
      .in(jsonBody[Expense].example(Expense.example))
      .out(stringBody)

  val deleteExpense: PublicEndpoint[Int, ApiError, String, Any] =
    apiEndpoint
      .in("expenses" / path[Int]("id"))
      .delete
      .out(stringBody)

  val expensesByLocation: PublicEndpoint[String, ApiError, List[Expense], Any] =
    apiEndpoint
      .in("expenses" / "filter" / path[String]("location"))
      .get
      .out(jsonBody[List[Expense]])
  val expensesByName: PublicEndpoint[String, ApiError, List[Expense], Any] =
    apiEndpoint
      .in("expenses" / "filter" / path[String]("name"))
      .get
      .out(jsonBody[List[Expense]])
  val expensesByAccount: PublicEndpoint[String, ApiError, List[Expense], Any] =
    apiEndpoint
      .in("expenses" / "filter" / path[String]("account"))
      .get
      .out(jsonBody[List[Expense]])
  val expensesByCategory: PublicEndpoint[String, ApiError, List[Expense], Any] =
    apiEndpoint
      .in("expenses" / "filter" / path[String]("category"))
      .get
      .out(jsonBody[List[Expense]])

  val getTotalAmountByName: PublicEndpoint[String, ApiError, String, Any] =
    apiEndpoint.get
      .in("expenses" / "total" / "name" / path[String]("name"))
      .out(stringBody)
  val getTotalAmountByCategory: PublicEndpoint[String, ApiError, String, Any] =
    apiEndpoint.get
      .in("expenses" / "total" / "category" / path[String]("category"))
      .out(stringBody)

  val getTotalAmountByLocation: PublicEndpoint[String, ApiError, String, Any] =
    apiEndpoint.get
      .in("expenses" / "total" / "location" / path[String]("location"))
      .out(stringBody)

  val getTotalAmountByAccount: PublicEndpoint[String, ApiError, String, Any] =
    apiEndpoint.get
      .in("expenses" / "total" / "account" / path[String]("account"))
      .out(stringBody)
}
