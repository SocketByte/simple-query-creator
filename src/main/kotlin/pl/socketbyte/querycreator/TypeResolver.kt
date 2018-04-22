package pl.socketbyte.querycreator

import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

object TypeResolver {
    fun resolve(clazz: Class<*>): String {
        return when (clazz) {
            String::class.java -> "TEXT"
            Int::class.java -> "INT"
            Long::class.java -> "BIGINT"
            UUID::class.java -> "CHAR(36)"
            Double::class.java -> "DOUBLE"
            Float::class.java -> "FLOAT"
            Short::class.java -> "TINYINT"
            Char::class.java -> "CHAR(1)"
            else -> "TINYTEXT"
        }
    }
}