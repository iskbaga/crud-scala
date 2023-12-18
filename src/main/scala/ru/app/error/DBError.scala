package ru.app.error

sealed trait DBError

final case class UnexpectedDbError(message: String) extends DBError

object DBError {
  case object ConnectionDBError extends DBError
  case object NotFoundDbError extends DBError
}
