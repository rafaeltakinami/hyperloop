package br.com.guiabolso.hyperloop.utils

import br.com.guiabolso.hyperloop.exceptions.InvalidInputException
import br.com.guiabolso.hyperloop.validation.exceptions.ValidationException
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

fun JsonElement.verifyBoolean() {
    val input = this as JsonPrimitive
    if (!input.isBoolean) {
        throw InvalidInputException("Input $input is not a boolean")
    }
}

fun JsonElement.verifyFloat() {
    try {
        this.asString.toFloat()
    } catch (exception: Exception) {
        throw InvalidInputException("Input $this is not a float")
    }
}

fun JsonElement.verifyInt() {
    try {
        this.asString.toInt()
    } catch (exception: Exception) {
        throw InvalidInputException("Input $this is not an int")
    }
}

fun JsonElement.verifyDouble() {
    try {
        this.asString.toDouble()
    } catch (exception: Exception) {
        throw InvalidInputException("Input $this is not a double")
    }
}
