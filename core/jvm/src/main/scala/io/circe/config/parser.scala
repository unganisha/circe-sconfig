package io.circe.config

import cats.ApplicativeError
import cats.data.ValidatedNel
import cats.instances.either.*
import cats.syntax.bifunctor.*
import cats.syntax.either.*
import io.circe.{Decoder, Error, Json, ParsingFailure}
import org.ekrich.config.ConfigFactory

import java.io.File

object parser extends ConfigParser {

  final def parse(): Either[ParsingFailure, Json] =
    toJson(ConfigFactory.load())

  final def parseFile(file: File): Either[ParsingFailure, Json] =
    toJson(ConfigFactory.parseFile(file))

  final def parsePath(path: String): Either[ParsingFailure, Json] =
    toJson(ConfigFactory.load(), Some(path))

  /**
   * Load the default configuration and decode an instance at a specific path.
   *
   * @example
   *   {{{
   * scala> import io.circe.generic.auto._
   * scala> case class ServerSettings(host: String, port: Int)
   * scala> case class HttpSettings(server: ServerSettings)
   * scala> case class AppSettings(http: HttpSettings)
   *
   * scala> parser.decode[AppSettings]()
   * res0: Either[io.circe.Error, AppSettings] = Right(AppSettings(HttpSettings(ServerSettings(localhost,8080))))
   *   }}}
   */
  final def decode[A: Decoder](): Either[Error, A] =
    finishDecode(parse())

  /**
   * Load configuration from file and decode an instance.
   *
   * @example
   *   {{{
   * scala> import io.circe.generic.auto._
   * scala> case class ServerSettings(host: String, port: Int)
   * scala> case class HttpSettings(server: ServerSettings)
   * scala> case class AppSettings(http: HttpSettings)
   *
   * scala> parser.decodeFile[AppSettings](new java.io.File(io.circe.config.build.Info.test_classDirectory.getPath + "/application.conf"))
   * res0: Either[io.circe.Error, AppSettings] = Right(AppSettings(HttpSettings(ServerSettings(localhost,8080))))
   *   }}}
   */
  final def decodeFile[A: Decoder](file: File): Either[Error, A] =
    finishDecode(parseFile(file))

  /**
   * Load the default configuration and decode an instance.
   *
   * @example
   *   {{{
   * scala> import io.circe.generic.auto._
   * scala> case class ServerSettings(host: String, port: Int)
   *
   * scala> parser.decodePath[ServerSettings]("http.server")
   * res0: Either[io.circe.Error, ServerSettings] = Right(ServerSettings(localhost,8080))
   *   }}}
   */
  final def decodePath[A: Decoder](path: String): Either[Error, A] =
    finishDecode(parsePath(path))

  final def decodeFileAccumulating[A: Decoder](file: File): ValidatedNel[Error, A] =
    finishDecodeAccumulating[A](parseFile(file))

  /**
   * Load default configuration and decode an instance supporting [[cats.ApplicativeError]].
   *
   * @example
   *   {{{
   * scala> import io.circe.generic.auto._
   * scala> case class ServerSettings(host: String, port: Int)
   * scala> case class HttpSettings(server: ServerSettings)
   * scala> case class AppSettings(http: HttpSettings)
   *
   * scala> import cats.effect.IO
   * scala> parser.decodeF[IO, AppSettings]()
   * res0: cats.effect.IO[AppSettings] = IO(AppSettings(HttpSettings(ServerSettings(localhost,8080))))
   *   }}}
   */
  final def decodeF[F[_], A: Decoder]()(implicit ev: ApplicativeError[F, Throwable]): F[A] =
    decode[A]().leftWiden[Throwable].liftTo[F]

  /**
   * Load default configuration and decode an instance supporting [[cats.ApplicativeError]] at a specific path.
   *
   * @example
   *   {{{
   * scala> import io.circe.generic.auto._
   * scala> case class ServerSettings(host: String, port: Int)
   *
   * scala> import cats.effect.IO
   * scala> parser.decodePathF[IO, ServerSettings]("http.server")
   * res0: cats.effect.IO[ServerSettings] = IO(ServerSettings(localhost,8080))
   *   }}}
   */
  final def decodePathF[F[_], A: Decoder](path: String)(implicit ev: ApplicativeError[F, Throwable]): F[A] =
    decodePath[A](path).leftWiden[Throwable].liftTo[F]

}
