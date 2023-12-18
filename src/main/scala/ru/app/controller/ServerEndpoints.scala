package ru.app.controller

import cats.implicits.catsSyntaxApplicativeId
import cats.{Applicative, Monad}
import ru.app.controller.endpoint.ExpenseEndpoints._
import ru.app.service.{ExpenseService, ExpenseServiceImpl}
import sttp.tapir.server.ServerEndpoint

final class ServerEndpoints[F[_]: Applicative](
  userService: ExpenseService[F]
) {
  val helloServerEndpoint: ServerEndpoint[Any, F] =
    helloEndpoint
      .serverLogicSuccess { Expense =>
        s"Hello ${Expense.userName.name}".pure[F]
      }
  val getAllExpensesServerEndpoint: ServerEndpoint[Any, F] =
    allExpenses
      .serverLogic(_ => userService.getAllExpenses)

  val getExpenseByIdServerEndpoint: ServerEndpoint[Any, F] =
    expenseById
      .serverLogic(id => userService.getExpenseById(id))

  val addExpenseServerEndpoint: ServerEndpoint[Any, F] =
    addExpenses
      .serverLogic(newExpense => userService.addExpense(newExpense))

  val updateExpenseServerEndpoint: ServerEndpoint[Any, F] =
    updateExpense
      .serverLogic { case (id, updatedExpense) =>
        userService.updateExpense(id, updatedExpense)
      }

  val deleteExpenseServerEndpoint: ServerEndpoint[Any, F] =
    deleteExpense
      .serverLogic(id => userService.deleteExpense(id))

  val getLocationServerEndpoint: ServerEndpoint[Any, F] =
    expensesByLocation
      .serverLogic(location => userService.getExpensesByLocation(location))

  val getCategoryServerEndpoint: ServerEndpoint[Any, F] =
    expensesByCategory
      .serverLogic(category => userService.getExpensesByCategory(category))

  val getAccountServerEndpoint: ServerEndpoint[Any, F] =
    expensesByAccount
      .serverLogic(account => userService.getExpensesByAccount(account))

  val getUserServerEndpoint: ServerEndpoint[Any, F] =
    expensesByName
      .serverLogic(name => userService.getExpensesByName(name))

  val getAmountByUserServerEndpoint: ServerEndpoint[Any, F] =
    getTotalAmountByName
      .serverLogic(name => userService.getTotalAmountByName(name))

  val getAmountByLocationServerEndpoint: ServerEndpoint[Any, F] =
    getTotalAmountByLocation
      .serverLogic(location => userService.getTotalAmountByLocation(location))

  val getAmountByCategoryServerEndpoint: ServerEndpoint[Any, F] =
    getTotalAmountByCategory
      .serverLogic(category => userService.getTotalAmountByCategory(category))

  val getAmountByAccountServerEndpoint: ServerEndpoint[Any, F] =
    getTotalAmountByAccount
      .serverLogic(account => userService.getTotalAmountByAccount(account))

  val apiEndpoints: List[ServerEndpoint[Any, F]] =
    List(
      addExpenseServerEndpoint,
      updateExpenseServerEndpoint,
      deleteExpenseServerEndpoint,
      helloServerEndpoint,
      getAllExpensesServerEndpoint,
      getExpenseByIdServerEndpoint,
      getLocationServerEndpoint,
      getAccountServerEndpoint,
      getUserServerEndpoint,
      getCategoryServerEndpoint,
      getAmountByLocationServerEndpoint,
      getAmountByAccountServerEndpoint,
      getAmountByUserServerEndpoint,
      getAmountByCategoryServerEndpoint
    )
}

object ServerEndpoints {
  def stub[F[_]: Monad](): ServerEndpoints[F] =
    new ServerEndpoints(ExpenseServiceImpl.stub())
}
