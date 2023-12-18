package ru.app.service

import cats.Monad
import ru.app.error.DBError.{ConnectionDBError, NotFoundDbError}
import ru.app.error._
import ru.app.model.domain._
import ru.app.repository.expense.{ExpenseStorage, ExpenseStorageStub}
import ru.app.repository.model.domain.ExpenseElement
import ru.app.service.ExpenseServiceImpl.fromDBError
import tofu.syntax.feither.EitherFOps

final class ExpenseServiceImpl[F[_]: Monad](
  repository: ExpenseStorage[F]
) extends ExpenseService[F] {
  private def mapExpense(expenseDB: ExpenseElement): Expense =
    Expense(
      UserName(expenseDB.userName),
      ExpenseDescription(expenseDB.description),
      ExpenseAmount(expenseDB.integerPart, expenseDB.fractionalPart),
      ExpenseCategory(expenseDB.category),
      ExpenseLocation(expenseDB.location),
      ExpenseAccount(expenseDB.account),
      Date(expenseDB.timestamp)
    )
  override def getAllExpenses: F[Either[ApiError, List[Expense]]] =
    repository
      .getAll
      .mapIn(_.map(mapExpense)).leftMapIn(fromDBError)

  override def getExpenseById(id: Int): F[Either[ApiError, Expense]] =
    repository
      .getById(id)
      .mapIn(mapExpense).leftMapIn(fromDBError)

  override def addExpense(expense: Expense): F[Either[ApiError, String]] =
    repository
      .addExpense(expense)
      .mapIn(_.toString)
      .leftMapIn(fromDBError)

  override def updateExpense(id: Int, updatedExpense: Expense): F[Either[ApiError, String]] =
    repository
      .updateExpense(id, updatedExpense)
      .mapIn(_.toString)
      .leftMapIn(fromDBError)

  override def deleteExpense(id: Int): F[Either[ApiError, String]] =
    repository
      .deleteExpense(id)
      .mapIn(_.toString)
      .leftMapIn(fromDBError)

  override def getExpensesByLocation(location: String): F[Either[ApiError, List[Expense]]] =
    repository
      .getByLocation(location)
      .mapIn(_.map(mapExpense)).leftMapIn(fromDBError)

  override def getExpensesByAccount(account: String): F[Either[ApiError, List[Expense]]] =
    repository
      .getByAccount(account)
      .mapIn(_.map(mapExpense)).leftMapIn(fromDBError)

  override def getExpensesByCategory(category: String): F[Either[ApiError, List[Expense]]] =
    repository
      .getByCategory(category)
      .mapIn(_.map(mapExpense)).leftMapIn(fromDBError)

  override def getExpensesByName(name: String): F[Either[ApiError, List[Expense]]] =
    repository
      .getByUser(name)
      .mapIn(_.map(mapExpense)).leftMapIn(fromDBError)

  def getTotalAmountByCategory(category: String): F[Either[ApiError, String]] =
    repository
      .getAmountByCategory(category)
      .mapIn(_.toString)
      .leftMapIn(fromDBError)

  def getTotalAmountByLocation(location: String): F[Either[ApiError, String]] =
    repository
      .getAmountByLocation(location)
      .mapIn(_.toString)
      .leftMapIn(fromDBError)

  def getTotalAmountByName(name: String): F[Either[ApiError, String]] =
    repository
      .getAmountByUser(name)
      .mapIn(_.toString)
      .leftMapIn(fromDBError)

  def getTotalAmountByAccount(account: String): F[Either[ApiError, String]] =
    repository
      .getAmountByAccount(account)
      .mapIn(_.toString)
      .leftMapIn(fromDBError)
}

object ExpenseServiceImpl {
  def stub[F[_]: Monad]() = new ExpenseServiceImpl[F](new ExpenseStorageStub)

  def fromDBError(error: DBError): ApiError =
    error match {
      case UnexpectedDbError(_) => ServerApiError("Непредвиденная ошибка, попробуйте позже")
      case ConnectionDBError    => ServerApiError("Непредвиденная ошибка, попробуйте позже")
      case NotFoundDbError      => BusinessApiError("По данному запросу ничего не найдено")
    }
}
