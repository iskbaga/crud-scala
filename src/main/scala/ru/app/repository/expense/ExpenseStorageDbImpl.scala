package ru.app.repository.expense
import cats.effect.kernel.MonadCancelThrow
import cats.implicits.catsSyntaxApplicativeError
import doobie.implicits.{toConnectionIOOps, toSqlInterpolator}
import doobie.util.transactor.Transactor
import ru.app.error.DBError.NotFoundDbError
import ru.app.error.{DBError, UnexpectedDbError}
import ru.app.model.domain.Expense
import ru.app.repository.model.domain.ExpenseElement
import tofu.syntax.feither.EitherFOps

final class ExpenseStorageDbImpl[F[_]: MonadCancelThrow](
  transactor: Transactor[F]
) extends ExpenseStorage[F] {

  override def getAll: F[Either[DBError, List[ExpenseElement]]] =
    sql"select * from expenses"
      .query[ExpenseElement]
      .to[List]
      .transact(transactor)
      .attempt
      .leftMapIn(err => UnexpectedDbError(err.getMessage))

  override def getById(id: Int): F[Either[DBError, ExpenseElement]] =
    sql"select * from expenses where id = $id"
      .query[ExpenseElement]
      .option
      .transact(transactor)
      .attempt
      .leftMapIn(err => UnexpectedDbError(err.getMessage))
      .flatMapIn(_.toRight(NotFoundDbError))

  override def addExpense(expense: Expense): F[Either[DBError, Int]] =
    sql"insert into expenses (userName, description, amount_integer, amount_fraction, category, location, account, expenseTime) values (${expense.userName.name}, ${expense.description.value}, ${expense.amount.integerPart}, ${expense.amount.fractionalPart}, ${expense.category.value}, ${expense.location.value}, ${expense.account.value}, ${expense.date.date})"
      .update
      .withUniqueGeneratedKeys[Int]("ID")
      .transact(transactor)
      .attempt
      .leftMapIn(err => UnexpectedDbError(err.getMessage))
  override def updateExpense(id: Int, updatedExpense: Expense): F[Either[DBError, String]] =
    sql"update expenses set userName = ${updatedExpense.userName.name}, description = ${updatedExpense.description.value}, amount_integer = ${updatedExpense.amount.integerPart}, amount_fraction = ${updatedExpense.amount.fractionalPart}, category = ${updatedExpense.category.value}, location = ${updatedExpense.location.value}, account = ${updatedExpense.account.value}, expenseTime = ${updatedExpense.date.date} where id = $id"
      .update
      .run
      .transact(transactor)
      .attempt
      .leftMapIn(err => UnexpectedDbError(err.getMessage))
      .flatMapIn {
        case 0 =>
          Right(s"Запись траты под номером $id не найдена")
        case _ => Right("Запись траты обновлена успешно")
      }

  override def deleteExpense(id: Int): F[Either[DBError, String]] =
    sql"delete from expenses where id = $id"
      .update
      .run
      .transact(transactor)
      .attempt
      .leftMapIn(err => UnexpectedDbError(err.getMessage))
      .flatMapIn {
        case 0 => Right(s"Запись траты под номером $id не найдена")
        case _ => Right("Запись траты удалена успешно")
      }

  override def getByLocation(location: String): F[Either[DBError, List[ExpenseElement]]] =
    sql"select * from expenses where location = $location"
      .query[ExpenseElement]
      .to[List]
      .transact(transactor)
      .attempt
      .leftMapIn(err => UnexpectedDbError(err.getMessage))

  override def getByAccount(account: String): F[Either[DBError, List[ExpenseElement]]] =
    sql"select * from expenses where account = $account"
      .query[ExpenseElement]
      .to[List]
      .transact(transactor)
      .attempt
      .leftMapIn(err => UnexpectedDbError(err.getMessage))

  override def getByUser(userName: String): F[Either[DBError, List[ExpenseElement]]] =
    sql"select * from expenses where userName = $userName"
      .query[ExpenseElement]
      .to[List]
      .transact(transactor)
      .attempt
      .leftMapIn(err => UnexpectedDbError(err.getMessage))

  override def getByCategory(category: String): F[Either[DBError, List[ExpenseElement]]] =
    sql"select * from expenses where category = $category"
      .query[ExpenseElement]
      .to[List]
      .transact(transactor)
      .attempt
      .leftMapIn(err => UnexpectedDbError(err.getMessage))

  override def getAmountByLocation(location: String): F[Either[DBError, BigDecimal]] =
    sql"select COALESCE(SUM(COALESCE(amount_integer, 0) + COALESCE(amount_fraction, 0) / 100.0), 0)  as total_amount from expenses where location = $location"
      .query[BigDecimal]
      .option
      .transact(transactor)
      .attempt
      .leftMapIn(err => UnexpectedDbError(err.getMessage))
      .flatMapIn(_.toRight(NotFoundDbError))

  override def getAmountByAccount(account: String): F[Either[DBError, BigDecimal]] =
    sql"select COALESCE(SUM(COALESCE(amount_integer, 0) + COALESCE(amount_fraction, 0) / 100.0), 0)  as total_amount from expenses where account = $account"
      .query[BigDecimal]
      .option
      .transact(transactor)
      .attempt
      .leftMapIn(err => UnexpectedDbError(err.getMessage))
      .flatMapIn(_.toRight(NotFoundDbError))

  override def getAmountByUser(userName: String): F[Either[DBError, BigDecimal]] =
    sql"select COALESCE(SUM(COALESCE(amount_integer, 0) + COALESCE(amount_fraction, 0) / 100.0), 0)  as total_amount from expenses where userName = $userName"
      .query[BigDecimal]
      .option
      .transact(transactor)
      .attempt
      .leftMapIn(err => UnexpectedDbError(err.getMessage))
      .flatMapIn(_.toRight(NotFoundDbError))

  override def getAmountByCategory(category: String): F[Either[DBError, BigDecimal]] =
    sql"select COALESCE(SUM(COALESCE(amount_integer, 0) + COALESCE(amount_fraction, 0) / 100.0), 0)  as total_amount from expenses where category = $category"
      .query[BigDecimal]
      .option
      .transact(transactor)
      .attempt
      .leftMapIn(err => UnexpectedDbError(err.getMessage))
      .flatMapIn(_.toRight(NotFoundDbError))
}
