package me.cooper.rick.crowdcontrollerserver.util

import java.util.*

object RandomPasswordGenerator {

    private const val ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private const val DEFAULT_LENGTH = 12
    private val RANDOM = Random()

    fun generatePassword(length: Int= DEFAULT_LENGTH): String  {
        return (0 until length)
                .map { ALPHABET[RANDOM.nextInt(ALPHABET.length)] }
                .joinToString("")
    }

}
