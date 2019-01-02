package io.samborskii.nusbus.util

fun String.removeSpecification(): String {
    val bracketIndex = indexOf("(")
    return if (bracketIndex != -1) substring(0, bracketIndex).trimEnd() else this
}

fun String.hasSpecification(): Boolean = contains("(")
