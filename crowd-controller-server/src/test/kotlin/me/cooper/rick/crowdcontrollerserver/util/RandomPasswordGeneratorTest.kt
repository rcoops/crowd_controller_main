package me.cooper.rick.crowdcontrollerserver.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

internal class RandomPasswordGeneratorTest {

    @Test
    fun testGeneratePassword() {
        // Given a length of password
        val passwordLength = 10

        // When generating a password
        val password = RandomPasswordGenerator.generatePassword(passwordLength)

        // Then it's the correct length
        assertEquals(passwordLength, password.length)

        // And it contains only alphanumeric characters
        assertFalse(password.contains("[^a-zA-Z0-9]".toRegex()))
    }

}
