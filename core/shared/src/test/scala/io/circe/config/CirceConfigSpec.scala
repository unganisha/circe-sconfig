/*
 * Copyright 2017 Jonas Fonseca
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.circe.config

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import org.ekrich.config.{parser => _, _}
import io.circe.{parser => _, _}

import scala.concurrent.duration._
import java.time.Period
import io.circe.config.syntax._
import io.circe.config.Resources
import io.circe.config.Fixtures._

class CirceConfigSpec extends AnyFlatSpec with Matchers {
  import CirceConfigSpec._

  "parser" should "parse and decode config from string" in new ParserTests {
    def parse = parser.parse(AppConfigString)
    def decode = parser.decode[TestConfig](AppConfigString)
  }

  it should "parse and decode config from object" in new ParserTests {
    def parse = parser.parse(AppConfig)
    def decode = parser.decode[TestConfig](AppConfig)
  }

  "printer" should "print it into a config string" in {
    val Right(json) = parser.parse(AppConfig)
    val expected = readFile("CirceConfigSpec.printed.conf")
    assert(printer.print(json).trim == expected.trim)
  }

  "syntax" should "provide Config decoder" in {
    assert(AppConfig.as[TestConfig] == Right(DecodedTestConfig))
  }

  it should "provide syntax to decode at a given path" in {
    assert(AppConfig.as[Nested]("e") == Right(Nested(true)))
  }

  "round-trip" should "parse and print" in {
    for (file <- Resources.listFiles(resourcesDir)) {
      val Right(json) = parser.parse(Resources.readFile(file))
      assert(parser.parse(printer.print(json)) == Right(json), s"round-trip failed for $file")
    }
  }

}

object CirceConfigSpec {
  val classesDir: String = Resources.testClassesDirectory
  val resourcesDir: String = Resources.sharedTestResourcesDirectory
  def readFile(path: String): String = Resources.readResourceFile(path)

  val AppConfig: Config = ConfigFactory.parseString(readFile("CirceConfigSpec.conf"))
  val AppConfigString: String = readFile("CirceConfigSpec.conf")

  trait ParserTests {
    def parse: Either[ParsingFailure, Json]
    def decode: Either[Error, TestConfig]

    assert(parse.isRight)

    val Right(config) = decode

    assert(config == DecodedTestConfig)
    assert(config.k.getDouble("ka") == 1.1)
    assert(config.k.getString("kb") == "abc")
    assert(config.l.unwrapped == "localhost")
  }

  val DecodedAppSettings = AppSettings(
    HttpSettings(
      1.1,
      ServerSettings(
        "localhost",
        8080,
        5 seconds,
        ConfigMemorySize.ofBytes(5242880)
      )
    )
  )

  val DecodedTestConfig = TestConfig(
    a = 42,
    b = false,
    c = "http://example.org",
    d = None,
    e = Nested(obj = true),
    f = List(0, .2, 123.4),
    g = List(List("nested", "list")),
    h = List(Nested(obj = true), Nested(obj = false)),
    i = 7357 seconds,
    j = ConfigMemorySize.ofBytes(134217728),
    k = ConfigFactory.parseString("ka = 1.1, kb = abc"),
    l = ConfigValueFactory.fromAnyRef("localhost"),
    m = TypeWithAdder(12),
    n = 0.0,
    o = 0,
    p = Period.ofWeeks(4)
  )
}
