package ru.app.service

import ru.app.error.ApiError
import ru.app.model.domain.Expense

trait ExpenseService[F[_]] {

  def addExpense(expense: Expense): F[Either[ApiError, String]]
  def getExpenseById(id: Int): F[Either[ApiError, Expense]]
  def getExpensesByName(name: String): F[Either[ApiError, List[Expense]]]
  def getExpensesByAccount(account: String): F[Either[ApiError, List[Expense]]]
  def getExpensesByLocation(location: String): F[Either[ApiError, List[Expense]]]
  def getExpensesByCategory(category: String): F[Either[ApiError, List[Expense]]]
  def getAllExpenses: F[Either[ApiError, List[Expense]]]
  def updateExpense(id: Int, updatedExpense: Expense): F[Either[ApiError, String]]
  def deleteExpense(id: Int): F[Either[ApiError, String]]

  def getTotalAmountByCategory(category: String): F[Either[ApiError, String]]

  def getTotalAmountByLocation(location: String): F[Either[ApiError, String]]

  def getTotalAmountByName(name: String): F[Either[ApiError, String]]
  def getTotalAmountByAccount(account: String): F[Either[ApiError, String]]
}
