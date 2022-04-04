package io.circe.config

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import io.circe.config.CirceConfigSpec.{AppConfig, DecodedTestConfig, Nested, TestConfig}
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

}
