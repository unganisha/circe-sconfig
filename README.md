# circe-config

[![CI Status]][CI]

Small library for translating between [HOCON], [Java properties], and JSON documents and circe's JSON AST. Forked
from [circe-config] to allow for Scala JS and Scala Native support via [sconfig]. All credit goes towards the original
developers.

At a high-level it can be used as a [circe] powered front-end for the [sconfig] library to enable boilerplate free
loading of settings into Scala types. More generally it provides parsers and printers for interoperating with
[Typesafe config]'s JSON AST.

 [HOCON]: https://github.com/lightbend/config/blob/master/HOCON.md
 [Java properties]: https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html

## Usage

To use this library configure your sbt project with the following line:

```sbt
libraryDependencies += "io.github.unganisha" %% "circe-sconfig" % "0.8.0"
```

## Documentation
Please refer to the original library documentation [here](https://circe.github.io/circe-config/io/circe/config/index.html).
This is to reduce the maintenance burden of this library.

### Non JVM Usage
Currently, the supported platforms include the  JVM and [Scala.js](https://www.scala-js.org/).

In the case of platforms other than a Java Virtual Machine, only a subset of the API is available. Specifically any
methods that make use of `java.io.File` and/or `java.net.URL` are not currently supported. Additionally, an
implementation of the `java.time` API must also be included, such as [sjavatime](https://github.com/ekrich/sjavatime)
for example.

## Examples
Please refer to the original library examples [here](https://github.com/circe/circe-config#example).

## Contributing

Contributions are very welcome. Please see [instructions](CONTRIBUTING.md) on
how to create issues and submit patches.

## License
circe-config is licensed under the **[Apache License, Version 2.0][apache]** (the
"License"); you may not use this software except in compliance with the License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 [apache]: http://www.apache.org/licenses/LICENSE-2.0
 [circe]: https://github.com/circe/circe
 [circe-config]: https://github.com/circe/circe-config
 [sconfig]: https://github.com/ekrich/sconfig
 [Typesafe config]: https://github.com/lightbend/config
 [CI]: https://github.com/unganisha/circe-sconfig/actions
 [CI Status]: https://img.shields.io/github/workflow/status/unganisha/circe-sconfig/Continuous%20Integration.svg
 [Latest Version Badge]: https://img.shields.io/maven-central/v/io.circe/circe-config_2.12.svg
 [Latest Version]: https://maven-badges.herokuapp.com/maven-central/io.circe/circe-config_2.12
