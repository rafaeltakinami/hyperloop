package br.com.guiabolso.hyperloop.validation

import br.com.guiabolso.hyperloop.exceptions.InvalidInputException
import com.google.gson.JsonPrimitive
import java.text.ParseException
import java.text.SimpleDateFormat

enum class PrimitiveTypes {
    STRING {
        override fun verifyType(element: JsonPrimitive) {
            if (!element.isString) {
                throw InvalidInputException("Input $element is not a string")
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
    },
    DATETIME {
        override fun verifyType(element: JsonPrimitive) {
            val simpleDateFormat = SimpleDateFormat("//TODO: INSTANT DATE") // TODO: INSTANT FORMAT
            try {
                simpleDateFormat.parse(element.asString)
            } catch (e: ParseException) {
                throw InvalidInputException("Date Element '${element.asString}' is not a INSTANT DATE")
            }
        }
    };

    companion object {
        fun valueOfOrNull(value: String): PrimitiveTypes? {
            return try {
                PrimitiveTypes.valueOf(value)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    abstract fun verifyType(element: JsonPrimitive)
}