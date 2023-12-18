package ru.app.model

import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.syntax.EncoderOps
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.{Codec, DecodeResult, Schema, Validator}
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

object domain {

  case class ExpenseDescription(value: String)
  case class ExpenseAmount(integerPart: Long, fractionalPart: Int)
  case class ExpenseCategory(value: String)
  case class ExpenseLocation(value: String)
  case class ExpenseAccount(value: String)
  case class UserName(name: String)
  case class Date(date: String)

  case class Expense(
    userName: UserName,
    description: ExpenseDescription,
    amount: ExpenseAmount,
    category: ExpenseCategory,
    location: ExpenseLocation,
    account: ExpenseAccount,
    date: Date
  )

  object Expense {

    implicit val descriptionSchema: Schema[ExpenseDescription] =
      Schema
        .derived[ExpenseDescription]
        .modify(_.value)(_.description("Expense description "))
        .validate(Validator
          .all[String](
            Validator.minLength(0),
            Validator.maxLength(20)
          )
          .contramap[ExpenseDescription](_.value))

    implicit val amountSchema: Schema[ExpenseAmount] =
      Schema
        .derived[ExpenseAmount]
        .description("Expense amount")
        .validate(Validator.min(0L).contramap(_.integerPart))
        .validate(Validator.min(0).contramap(_.fractionalPart))
        .validate(Validator.max(99).contramap(_.fractionalPart))

    implicit val categorySchema: Schema[ExpenseCategory] =
      Schema
        .derived[ExpenseCategory]
        .modify(_.value)(_.description("Expense category"))
        .validate(Validator
          .all[String](
            Validator.minLength(0),
            Validator.maxLength(20)
          )
          .contramap[ExpenseCategory](_.value))

    implicit val locationSchema: Schema[ExpenseLocation] =
      Schema
        .derived[ExpenseLocation]
        .modify(_.value)(_.description("Expense location"))
        .validate(Validator
          .all[String](
            Validator.minLength(0),
            Validator.maxLength(20)
          )
          .contramap[ExpenseLocation](_.value))

    implicit val accountSchema: Schema[ExpenseAccount] =
      Schema
        .derived[ExpenseAccount]
        .modify(_.value)(_.description("Account from which the money was spent"))
        .validate(Validator
          .all[String](
            Validator.minLength(0),
            Validator.maxLength(20)
          )
          .contramap[ExpenseAccount](_.value))

    implicit val userNameSchema: Schema[UserName] =
      Schema
        .derived[UserName]
        .description("User name")
        .validate(Validator.minLength(2).contramap(_.name))
    val timestampValidator: Validator[Date] =
      Validator.pattern(
        "^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[0-2])\\.(\\d{4})$"
      ).contramap(_.date)

    implicit val dateSchema: Schema[Date] =
      Schema
        .derived[Date]
        .description("Date")
        .validate(timestampValidator)
    implicit val schema: Schema[Expense] =
      Schema
        .derived[Expense]

    implicit val nameWriter: JsonWriter[UserName]                  = jsonWriter
    implicit val descriptionWriter: JsonWriter[ExpenseDescription] = jsonWriter
    implicit val amountWriter: JsonWriter[ExpenseAmount]           = jsonWriter
    implicit val categoryWriter: JsonWriter[ExpenseCategory]       = jsonWriter
    implicit val locationWriter: JsonWriter[ExpenseLocation]       = jsonWriter
    implicit val accountWriter: JsonWriter[ExpenseAccount]         = jsonWriter
    implicit val dateWriter: JsonWriter[Date]                      = jsonWriter

    implicit val nameReader: JsonReader[UserName]                  = jsonReader
    implicit val descriptionReader: JsonReader[ExpenseDescription] = jsonReader
    implicit val amountReader: JsonReader[ExpenseAmount]           = jsonReader
    implicit val categoryReader: JsonReader[ExpenseCategory]       = jsonReader
    implicit val locationReader: JsonReader[ExpenseLocation]       = jsonReader
    implicit val accountReader: JsonReader[ExpenseAccount]         = jsonReader
    implicit val dateReader: JsonReader[Date]                      = jsonReader

    implicit val ExpenseWriter: JsonWriter[Expense] = jsonWriter
    implicit val ExpenseReader: JsonReader[Expense] = jsonReader
    implicit val ExpenseCodec: PlainCodec[Expense] = Codec.string
      .mapDecode { rawJson =>
        io.circe.parser.decode[Expense](rawJson) match {
          case Left(error)    => DecodeResult.Error(rawJson, error)
          case Right(example) => DecodeResult.Value(example)
        }
      }(_.asJson.noSpaces)

    val example: Expense = Expense(
      UserName("Bob Johnson"),
      ExpenseDescription("Gasoline"),
      ExpenseAmount(123, 0),
      ExpenseCategory("Transportation"),
      ExpenseLocation("Gas Station"),
      ExpenseAccount("Credit Card"),
      Date("06.07.2004")
    )
  }
}
