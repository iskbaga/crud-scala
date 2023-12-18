package ru.app

import cats.effect.std.{Dispatcher, Env}
import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.config.{Config, ConfigFactory}
import doobie.Transactor
import io.vertx.core.http.HttpServer
import io.vertx.core.{Future, Vertx}
import io.vertx.ext.web.Router
import ru.app.controller.ServerEndpoints
import ru.app.repository.expense.ExpenseStorageDbImpl
import ru.app.service.ExpenseServiceImpl
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.server.vertx.cats.VertxCatsServerInterpreter._
import sttp.tapir.server.vertx.cats.{VertxCatsServerInterpreter, VertxCatsServerOptions}
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import tofu.syntax.foption.FOptionSyntax

object Application extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val config: Config = ConfigFactory.load()

    val httpHost: String   = config.getString("http.host")
    val httpPort: Int      = config.getInt("http.port")
    val dbDriver: String   = config.getString("database.driver")
    val dbUrl: String      = config.getString("database.url")
    val dbUser: String     = config.getString("database.user")
    val dbPassword: String = config.getString("database.password")

    val vertx  = Vertx.vertx()
    val server = vertx.createHttpServer()
    val router = Router.router(vertx)

    val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
      driver = dbDriver,
      url    = dbUrl,
      user   = dbUser,
      pass   = dbPassword
    )

    val prometheusMetrics: PrometheusMetrics[IO] = PrometheusMetrics.default[IO]()
    val serverEndpoint                           = new ServerEndpoints(new ExpenseServiceImpl(new ExpenseStorageDbImpl[IO](xa)))
    val endpoints: List[ServerEndpoint[Any, IO]] = {
      val docEndpoints: List[ServerEndpoint[Any, IO]] = SwaggerInterpreter()
        .fromServerEndpoints[IO](serverEndpoint.apiEndpoints, "crud", "1.0.0")

      val metricsEndpoint: ServerEndpoint[Any, IO] = prometheusMetrics.metricsEndpoint

      serverEndpoint.apiEndpoints ++ docEndpoints ++ List(metricsEndpoint)
    }

    Dispatcher
      .parallel[IO]
      .map { d: Dispatcher[IO] =>
        VertxCatsServerOptions
          .customiseInterceptors(d)
          .metricsInterceptor(prometheusMetrics.metricsInterceptor())
          .options
      }
      .use { serverOptions =>
        for {
          port <- Env[IO].get("HTTP_PORT").flatMapIn(_.toIntOption).map(_.getOrElse(httpPort))
          _    <- LiquibaseMigration.run[IO](xa)
          bind <- IO
            .delay {
              endpoints
                .foreach { endpoint =>
                  VertxCatsServerInterpreter[IO](serverOptions)
                    .route(endpoint)(router)
                }
              server.requestHandler(router).listen(port)
            }
            .flatMap((t: Future[HttpServer]) => t.asF[IO])
          _ <-
            IO.println(s"Go to http://${httpHost}:${bind.actualPort()}/docs to open SwaggerUI.")
          _ <- IO.readLine
          _ <- IO.delay(server.close).flatMap(_.asF[IO].void)
        } yield bind
      }
      .as(ExitCode.Success)
  }
}
