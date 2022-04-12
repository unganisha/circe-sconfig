package io.circe.config

import io.circe.*
import io.circe.Decoder.Result
import org.ekrich.config.{Config, ConfigMemorySize, ConfigValue}
import io.circe.generic.semiauto.*

import java.time.Period
import scala.concurrent.duration.FiniteDuration

import io.circe.config.syntax.*

object Fixtures {

  sealed abstract class Adder[T] {
    def add(a: T, b: T): T
  }
  implicit def numericAdder[T: scala.math.Numeric]: Adder[T] = new Adder[T] {
    override def add(a: T, b: T): T = implicitly[scala.math.Numeric[T]].plus(a, b)
  }

  case class TypeWithAdder[T: Adder](typeWithAdder: T)

  case class Nested(obj: Boolean)

  case class TestConfig(
    a: Int,
    b: Boolean,
    c: String,
    d: Option[String],
    e: Nested,
    f: List[Double],
    g: List[List[String]],
    h: List[Nested],
    i: FiniteDuration,
    j: ConfigMemorySize,
    k: Config,
    l: ConfigValue,
    m: TypeWithAdder[Int],
    n: Double,
    o: Double,
    p: Period
  )

  case class ServerSettings(host: String, port: Int, timeout: FiniteDuration, maxUpload: ConfigMemorySize)

  case class HttpSettings(version: Double, server: ServerSettings)

  case class AppSettings(http: HttpSettings)

  implicit val nestedDecoder: Decoder[Nested] = deriveDecoder

  implicit val testConfigDecoder: Decoder[TestConfig] = deriveDecoder

  implicit val serverSettingsDecoder: Decoder[ServerSettings] = deriveDecoder

  implicit val httpSettingsDecoder: Decoder[HttpSettings] = deriveDecoder

  implicit val appSettingsDecoder: Decoder[AppSettings] = deriveDecoder

  implicit def typeWithAdderDecoder[A: Adder](implicit aDecoder: Decoder[A]): Decoder[TypeWithAdder[A]] =
    new Decoder[TypeWithAdder[A]] {
      override def apply(c: HCursor): Result[TypeWithAdder[A]] = for {
        typeWithAdder <- c.downField("typeWithAdder").as[A]
      } yield TypeWithAdder(typeWithAdder)
    }

}
