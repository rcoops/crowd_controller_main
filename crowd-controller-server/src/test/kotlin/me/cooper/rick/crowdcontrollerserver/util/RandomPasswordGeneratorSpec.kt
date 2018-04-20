package me.cooper.rick.crowdcontrollerserver.util

import junit.framework.Assert.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

internal object RandomPasswordGeneratorSpec : Spek({
    for (i in 0..500) {
        given("a length of password") {
            val passwordLength = 10
            on("generating a password") {
                val password = RandomPasswordGenerator.generatePassword(passwordLength)
                it("is the correct length") {
                    assertEquals(passwordLength, password.length)
                }
                it("contains only alphanumeric characters") {
                    assertFalse(password.contains("[^a-zA-Z0-9]".toRegex()))
                }
            }
        }
    }
})
