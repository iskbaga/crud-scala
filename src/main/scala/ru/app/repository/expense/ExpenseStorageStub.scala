package ru.app.repository.expense

import cats.Applicative
import cats.implicits.catsSyntaxApplicativeId
import ru.app.error.DBError
import ru.app.error.DBError.NotFoundDbError
import ru.app.model.domain.Expense
import ru.app.repository.expense.ExpenseStorageStub.storage
import ru.app.repository.model.domain.ExpenseElement
import tofu.syntax.feither.EitherIdFOps

final class ExpenseStorageStub[F[_]: Applicative]
    extends ExpenseStorage[F] {

  override def getAll: F[Either[DBError, List[ExpenseElement]]] =
    storage.asRightF[F, DBError]

  override def getById(id: Int): F[Either[DBError, ExpenseElement]] =
    storage
      .find(_.id == id)
      .toRight[DBError](NotFoundDbError)
      .pure[F]

  override def addExpense(expense: Expense): F[Either[DBError, Int]] =
    42.asRightF[F, DBError]

  override def updateExpense(id: Int, expense: Expense): F[Either[DBError, String]] =
    "a".asRightF[F, DBError]

  override def deleteExpense(id: Int): F[Either[DBError, String]] =
    "b".asRightF[F, DBError]

  override def getByLocation(location: String): F[Either[DBError, List[ExpenseElement]]] =
    storage
      .filter(_.location == location)
      .asRightF[F, DBError]

  override def getByAccount(account: String): F[Either[DBError, List[ExpenseElement]]] =
    storage
      .filter(_.account == account)
      .asRightF[F, DBError]
  override def getByUser(userName: String): F[Either[DBError, List[ExpenseElement]]] =
    storage
      .filter(_.userName == userName)
      .asRightF[F, DBError]

  override def getByCategory(category: String): F[Either[DBError, List[ExpenseElement]]] =
    storage
      .filter(_.category == category)
      .asRightF[F, DBError]

  override def getAmountByLocation(location: String): F[Either[DBError, BigDecimal]] =
    storage
      .filter(_.location == location)
      .map(expense => BigDecimal(expense.integerPart) + BigDecimal(expense.fractionalPart) / 100.0)
      .sum
      .asRightF[F, DBError]

  override def getAmountByUser(userName: String): F[Either[DBError, BigDecimal]] =
    storage
      .filter(_.userName == userName)
      .map(expense => BigDecimal(expense.integerPart) + BigDecimal(expense.fractionalPart) / 100.0)
      .sum
      .asRightF[F, DBError]

  override def getAmountByCategory(category: String): F[Either[DBError, BigDecimal]] =
    storage
      .filter(_.category == category)
      .map(expense => BigDecimal(expense.integerPart) + BigDecimal(expense.fractionalPart) / 100.0)
      .sum
      .asRightF[F, DBError]

  override def getAmountByAccount(account: String): F[Either[DBError, BigDecimal]] =
    storage
      .filter(_.account == account)
      .map(expense => BigDecimal(expense.integerPart) + BigDecimal(expense.fractionalPart) / 100.0)
      .sum
      .asRightF[F, DBError]
}

object ExpenseStorageStub {
  private val storage = List(
    ExpenseElement(1, "John Doe", "Groceries", 50, 25, "Food", "Local Supermarket", "Credit Card", "17.12.2022"),
    ExpenseElement(2, "Ivan Ivanov", "Dinner", 30, 75, "Dining", "Restaurant", "Cash", "06.12.2022"),
    ExpenseElement(3, "John Ivanov", "Gasoline", 40, 50, "Transportation", "Gas Station", "Credit Card", "19.11.2022"),
    ExpenseElement(4, "Sergey", "Electronics", 120, 0, "Shopping", "Tech Store", "Online Wallet", "06.07.2022"),
    ExpenseElement(5, "Ruslan", "Movie Night", 20, 50, "Entertainment", "Cinema", "Cash", "06.07.2022"),
    ExpenseElement(5, "Danil", "Movie Night", 20, 50, "Entertainment", "Cinema", "Cash", "06.07.2022")
  )
}
