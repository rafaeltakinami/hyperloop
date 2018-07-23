package br.com.guiabolso.hyperloop.validation.types

import br.com.guiabolso.hyperloop.exceptions.InvalidInputException
import br.com.guiabolso.hyperloop.validation.exceptions.ValidationException
import com.google.gson.JsonPrimitive

enum class PrimitiveTypes {
    STRING {
        override fun verifyType(element: JsonPrimitive) {
            if (!element.isString) {
                throw ValidationException("Input $element is not a string")
            }
        }
    },
    LONG {
        override fun verifyType(element: JsonPrimitive) {
            try {
                element.asString.toLong()
            } catch (exception: Exception) {
                throw InvalidInputException("Input $element is not a long")
            }
        }
    },
    INT {
        override fun verifyType(element: JsonPrimitive) {
            try {
                element.asString.toInt()
            } catch (exception: Exception) {
                throw InvalidInputException("Input $element is not an int")
            }
        }
    },
    FLOAT {
        override fun verifyType(element: JsonPrimitive) {
            try {
                element.asString.toFloat()
            } catch (exception: Exception) {
                throw InvalidInputException("Input $element is not a float")
            }
        }
    },
    DOUBLE {
        override fun verifyType(element: JsonPrimitive) {
            try {
                element.asString.toDouble()
            } catch (exception: Exception) {
                throw InvalidInputException("Input $element is not a double")
            }
        }
    },
    BOOLEAN {
        override fun verifyType(element: JsonPrimitive) {
            if (!element.isBoolean) {
                throw InvalidInputException("Input $element is not a boolean")
            }
        }
    };

    abstract fun verifyType(element: JsonPrimitive)
}