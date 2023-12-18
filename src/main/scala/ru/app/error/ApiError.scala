package ru.app.error

import sttp.tapir.Schema
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonObjectWriter, JsonReader}

sealed trait ApiError

final case class ServerApiError(
  message: String
) extends ApiError

final case class BusinessApiError(
  message: String
) extends ApiError

object ApiError {

  implicit val serverErrorWriter: JsonObjectWriter[ServerApiError] = jsonWriter
  implicit val serverErrorReader: JsonReader[ServerApiError]       = jsonReader
  implicit val serverErrorSchema: Schema[ServerApiError] =
    Schema
      .derived[ServerApiError]
      .modify(_.message)(_.description("Сообщение об ошибке"))
      .description("Внутренняя ошибка")

  implicit val businessErrorWriter: JsonObjectWriter[BusinessApiError] = jsonWriter
  implicit val businessErrorReader: JsonReader[BusinessApiError]       = jsonReader
  implicit val businessErrorSchema: Schema[BusinessApiError] =
    Schema
      .derived[BusinessApiError]
      .modify(_.message)(_.description("Сообщение об ошибке"))
      .description("Ошибка при обработке запроса")
}
