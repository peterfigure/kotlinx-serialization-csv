# Kotlin Serialization CSV

[![Maven Central](https://images1-focus-opensocial.googleusercontent.com/gadgets/proxy?container=focus&url=https%3A%2F%2Fimg.shields.io%2Fmaven-central%2Fv%2Fde.brudaswen.kotlinx.serialization%2Fkotlinx-serialization-csv%3Fstyle%3Dflat-square)](https://search.maven.org/artifact/de.brudaswen.kotlinx.serialization/kotlinx-serialization-csv)
![Snapshot](https://images1-focus-opensocial.googleusercontent.com/gadgets/proxy?container=focus&url=https%3A%2F%2Fimg.shields.io%2Fnexus%2Fs%2Fde.brudaswen.kotlinx.serialization%2Fkotlinx-serialization-csv%3Flabel%3Dsnapshot%26server%3Dhttps%253A%252F%252Foss.sonatype.org%26style%3Dflat-square)
[![CI Status](https://images1-focus-opensocial.googleusercontent.com/gadgets/proxy?container=focus&url=https%3A%2F%2Fimg.shields.io%2Fgithub%2Fworkflow%2Fstatus%2Fbrudaswen%2Fkotlinx-serialization-csv%2FCI%3Fstyle%3Dflat-square)](https://github.com/brudaswen/kotlinx-serialization-csv/actions?query=workflow%3ACI)
[![Codecov](https://images1-focus-opensocial.googleusercontent.com/gadgets/proxy?container=focus&url=https%3A%2F%2Fimg.shields.io%2Fcodecov%2Fc%2Fgithub%2Fbrudaswen%2Fkotlinx-serialization-csv%3Fstyle%3Dflat-square)](https://codecov.io/gh/brudaswen/kotlinx-serialization-csv)
[![License](https://images1-focus-opensocial.googleusercontent.com/gadgets/proxy?container=focus&url=https%3A%2F%2Fimg.shields.io%2Fgithub%2Flicense%2Fbrudaswen%2Fkotlinx-serialization-csv%3Fstyle%3Dflat-square)](https://www.apache.org/licenses/LICENSE-2.0)

Library to easily use *Kotlin Serialization* to serialize/parse CSV.

All types of record classes are supported (primitives, classes, enums, nested classes, ...).
However, CSV serialization works best if the column number if fixed. So, collections (lists, sets, maps) and 
open classes should be avoided.

## Gradle Dependencies
```kotlin
// Kotlin Serialization CSV
implementation("de.brudaswen.kotlinx.serialization:kotlinx-serialization-csv:0.1.0")

// Kotlin Serialization is added automatically, but can be added to force a specific version
implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
```

## Usage
First configure your project according to the 
[documentation](https://github.com/Kotlin/kotlinx.serialization#setup)
of the *Kotlin Serialization* library.

### CSV Example
```kotlin
@Serializable
data class Person(val nickname: String, val name: String?, val appearance: Appearance)

@Serializable
data class Appearance(val gender: Gender?, val age: Int?, val height: Double?)

@Serializable
enum class Gender { MALE, FEMALE }

fun main() {
    val csv = Csv(CsvConfiguration(hasHeaderRecord = true))

    val records = listOf(
        Person("Neo", "Thomas A. Anderson", Appearance(Gender.MALE, 37, 1.86)),
        Person("Trinity", null, Appearance(Gender.FEMALE, null, 1.74))
    )
    val serialized = csv.stringify(Person.serializer().list, records)
    println(serialized)
    // nickname,name,appearance.gender,appearance.age,appearance.height
    // Neo,Thomas A. Anderson,MALE,37,1.86
    // Trinity,,FEMALE,,1.74

    val input = """
        nickname,appearance.gender,appearance.height,appearance.age,name
        Neo,MALE,1.86,37,Thomas A. Anderson
        Trinity,FEMALE,1.74,,
    """.trimIndent().replace("\n", "\r\n")
    val parsed = csv.parse(Person.serializer().list, input)
    println(parsed)
    // [
    //   Person(nickname=Neo, name=Thomas A. Anderson, appearance=Appearance(gender=MALE, age=37, height=1.86)),
    //   Person(nickname=Trinity, name=null, appearance=Appearance(gender=FEMALE, age=null, height=1.74))
    // ]
}
```

### Pre-defined CSV formats
The library comes with multiple pre-defined formats that can be used out of the box.

| Config                 | Description |
|---                     |---          |
| `default`              | Standard Comma Separated Value format, as for `rfc4180` but allowing empty lines. *Format is unstable and may change in upcoming versions.* |
| `rfc4180`              | Comma separated format as defined by [RFC 4180](http://tools.ietf.org/html/rfc4180). |
| `excel`                | Excel file format (using a comma as the value delimiter). |

### Configuration
CSV serialization and parsing options can be changed by providing a custom `CsvConfiguration`.

| Option                 | Default Value  | Description |
|---                     |---             | ---         |
| `delimiter`            | `,`            | The delimiter character between columns. |
| `recordSeparator`      | `\r\n`         | The record separator. |
| `quoteChar`            | `"`            | The quote character used to quote column values. |
| `quoteMode`            | `MINIMAL`      | The quote mode used to decide if a column value should get quoted.<ul><li>`ALL`: Quotes *all* fields.</li><li>`ALL_NON_NULL`: Quotes all *non-null fields* and *fields which contain special characters*.</li><li>`ALL_NON_NUMERIC`: Quotes all *non-numeric fields* and *fields which contain special characters*.</li><li>`MINIMAL`: Quotes *fields which contain special characters*.</li><li>`NONE`: *Never* quotes fields (requires `CsvConfiguration.escapeChar` to be set).</li></ul> |
| `escapeChar`           | `null` (`\\` for `QuoteMode.NONE`) | The escape character used to escape reserved characters in a column value. |
| `nullString`           | *empty string* | The value to identify `null` values. |
| `unitString`           | `Unit`         | The value to identify `Unit` values. |
| `ignoreEmptyLines`     | `true`         | Ignore empty lines during parsing. |
| `hasHeaderRecord`      | `false`        | First line is header record. |
| `headerSeparator`      | `.`            | Character that is used to separate hierarchical header names. |
| `hasTrailingDelimiter` | `false`        | If records end with a trailing `delimiter`. |

## Requirements

| Dependency             | Versions |
|---                     |---       |
| *Kotlin Serialization* | 0.14.0   |

## License

```
Copyright 2020 Sven Obser

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
