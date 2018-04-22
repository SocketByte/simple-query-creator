package pl.socketbyte.querycreator.extensions

import pl.socketbyte.querycreator.QueryBuilder
import pl.socketbyte.querycreator.QueryProvider
import java.sql.Connection

fun <T> T.createQueryProvider(bind: Class<out T>, connection: Connection? = null, connectionPool: Boolean = false): QueryProvider<T> {
    return QueryProvider(this, bind, connection, connectionPool)
}