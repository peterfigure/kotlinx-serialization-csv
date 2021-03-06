/*
 * Copyright 2017-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.serialization.test

import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlin.test.assertEquals

inline fun <reified T : Any?> assertStringFormAndRestored(
    expected: String,
    original: T,
    serializer: KSerializer<T>,
    format: StringFormat,
    printResult: Boolean = false
) {
    val string = format.stringify(serializer, original)
    if (printResult) println("[Serialized form] $string")
    assertEquals(expected, string)
    val restored = format.parse(serializer, string)
    if (printResult) println("[Restored form] $restored")
    assertEquals(original, restored)
}

inline fun <reified T : Any> assertParse(
    input: String,
    expected: T,
    serializer: KSerializer<T>,
    format: StringFormat,
    printResult: Boolean = false
) {
    val restored = format.parse(serializer, input)
    if (printResult) println("[Restored form] $restored")
    assertEquals(expected, restored)
}

inline fun <reified T : Any> StringFormat.assertStringFormAndRestored(
    expected: String,
    original: T,
    serializer: KSerializer<T>,
    printResult: Boolean = false
) {
    val string = this.stringify(serializer, original)
    if (printResult) println("[Serialized form] $string")
    assertEquals(expected, string)
    val restored = this.parse(serializer, string)
    if (printResult) println("[Restored form] $restored")
    assertEquals(original, restored)
}

infix fun <T> T.shouldBe(expected: T) = assertEquals(expected, this)
