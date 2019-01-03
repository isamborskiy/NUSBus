package io.samborskii.nusbus.util

private val specificationRegex: Regex = "\\((.+?)\\)".toRegex()

fun String.removeSpecification(): String {
    val bracketIndex = indexOf("(")
    return if (bracketIndex != -1) substring(0, bracketIndex).trimEnd() else this
}

fun String.getSpecification(): String? = specificationRegex.find(this)?.groupValues?.last()
