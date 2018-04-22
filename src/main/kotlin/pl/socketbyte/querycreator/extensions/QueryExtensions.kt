package pl.socketbyte.querycreator.extensions

import pl.socketbyte.querycreator.QueryBuilder
import java.sql.Connection

/**
 * Create `QueryBuilder` class using `Connection`
 */
fun <T> Connection.build(bind: Class<out T>): QueryBuilder<T> {
    return QueryBuilder(bind)
}