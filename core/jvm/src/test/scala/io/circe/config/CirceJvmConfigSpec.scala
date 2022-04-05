package io.circe.config

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import io.circe.config.CirceConfigSpec.*
import io.circe.config.syntax.*
import io.circe.generic.auto.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CirceJvmConfigSpec extends AnyFlatSpec with Matchers {


  it should "provide Config decoder via ApplicativeError" in {
    assert(AppConfig.asF[IO, TestConfig].unsafeRunSync() == DecodedTestConfig)
  }

  it should "provide syntax to decode at a given path via ApplicativeError" in {
    assert(AppConfig.asF[IO, Nested]("e").unsafeRunSync() == Nested(true))
  }

  it should "parse and decode config from file" in new ParserTests {
    def file = Resources.resourceFile("CirceConfigSpec.conf")
    def parse = parser.parseFile(file)
    def decode = parser.decodeFile[TestConfig](file)
  }

  it should "parse and decode config from default typesafe config resolution" in {
    parser.decode[AppSettings]().fold(fail(_), _ should equal(DecodedAppSettings))
  }

  it should "parse and decode config from default typesafe config resolution via ApplicativeError" in {
    parser.decodeF[IO, AppSettings]().unsafeRunSync() should equal(DecodedAppSettings)
  }

  it should "parse and decode config from default typesafe config resolution with path via ApplicativeError" in {
    parser.decodePathF[IO, HttpSettings]("http").unsafeRunSync() should equal(DecodedAppSettings.http)
  }

}
