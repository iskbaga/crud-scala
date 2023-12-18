package ru.app.repository.model

import sttp.tapir.Schema
import tethys.derivation.semiauto._
import tethys.{JsonReader, JsonWriter}

import java.time.Instant

object domain {

  case class ExpenseElement(
    id: Int,
    userName: String,
    description: String,
    integerPart: Long,
    fractionalPart: Int,
    category: String,
    location: String,
    account: String,
    timestamp: String
  )

  object ExpenseElement {
    implicit val instantReader: JsonReader[Instant] = JsonReader.stringReader.map(Instant.parse)
    implicit val instantWriter: JsonWriter[Instant] = JsonWriter.stringWriter.contramap(_.toString)
    implicit val schema: Schema[ExpenseElement] =
      Schema
        .derived[ExpenseElement]
        .modify(_.timestamp)(_.description("Time of purchase"))

    implicit val ExpenseWriter: JsonWriter[ExpenseElement] = jsonWriter
    implicit val ExpenseReader: JsonReader[ExpenseElement] = jsonReader
  }
}
