package ru.app.repository.expense

import ru.app.error.DBError
import ru.app.model.domain.Expense
import ru.app.repository.model.domain.ExpenseElement

trait ExpenseStorage[F[_]] {

  def addExpense(expense: Expense): F[Either[DBError, Int]]

  def getById(id: Int): F[Either[DBError, ExpenseElement]]

  def getAll: F[Either[DBError, List[ExpenseElement]]]

  def updateExpense(id: Int, updatedExpense: Expense): F[Either[DBError, String]]

  def deleteExpense(id: Int): F[Either[DBError, String]]

  def getByLocation(location: String): F[Either[DBError, List[ExpenseElement]]]
  def getByAccount(account: String): F[Either[DBError, List[ExpenseElement]]]
  def getByUser(userName: String): F[Either[DBError, List[ExpenseElement]]]
  def getByCategory(category: String): F[Either[DBError, List[ExpenseElement]]]

  def getAmountByLocation(location: String): F[Either[DBError, BigDecimal]]

  def getAmountByAccount(account: String): F[Either[DBError, BigDecimal]]

  def getAmountByUser(userName: String): F[Either[DBError, BigDecimal]]

  def getAmountByCategory(category: String): F[Either[DBError, BigDecimal]]

}
