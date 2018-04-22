package pl.socketbyte.querycreator

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

open class QueryBuilder<T> (private val bind: Class<out T>) {

    private val query: StringBuilder = StringBuilder()
    private val replacements: MutableMap<Int, Any> = mutableMapOf()

    private var connectionPool: Boolean = false
    private var connection: Connection? = null
    private var statement: PreparedStatement? = null

    private var cursor: Int = 1
    private var target: String = bind.simpleName.toLowerCase() + "s"

    fun bind(connection: Connection, connectionPool: Boolean = false): QueryBuilder<T> {
        this.connection = connection
        this.connectionPool = connectionPool
        return this
    }

    fun target(target: String? = null): QueryBuilder<T> {
        if (target != null)
            this.target = target
        return this
    }

    fun table(vararg pairs: Pair<String, String>): QueryBuilder<T> {
        query.append("CREATE TABLE IF NOT EXIST $target (")
        for ((key, value) in pairs)
            query.append("$key $value, ")
        query.setLength(query.length - 2)
        query.append(")")
        return this
    }

    fun select(elements: String = "*"): QueryBuilder<T> {
        query.append("SELECT $elements ")
        return this
    }

    fun from(): QueryBuilder<T> {
        query.append("FROM $target ")
        return this
    }

    fun where(vararg pairs: Pair<String, Any>): QueryBuilder<T> {
        query.append("WHERE ")

        listElements(pairs.toList(), " AND ")
        return this
    }

    fun update(): QueryBuilder<T> {
        query.append("UPDATE $target ")
        return this
    }

    fun set(vararg pairs: Pair<String, Any>): QueryBuilder<T> {
        query.append("SET (")

        listElements(pairs.toList(), ", ")

        query.append(") ")
        return this
    }

    fun insert(): QueryBuilder<T> {
        query.append("INSERT INTO $target ")
        return this
    }

    fun values(vararg values: Any): QueryBuilder<T> {
        query.append("VALUES (")

        listValues(values.toList(), ", ")

        query.append(") ")
        return this
    }

    fun execute(): QueryBuilder<T> {
        createPreparedStatement()

        for ((index, value) in replacements)
            statement?.setObject(index, value)

        statement?.executeUpdate()
        return this
    }

    fun query(): ResultSet {
        createPreparedStatement()
        return statement?.executeQuery()
                ?: throw NullPointerException("result set can not be null")
    }

    private fun createPreparedStatement() {
        if (connection == null)
            throw IllegalArgumentException("no connection provided")

        statement = connection?.prepareStatement(query.toString())
        if (statement == null)
            throw NullPointerException("statement can not be null")

        replacements.forEach { (key, value) ->
            statement?.setObject(key, value)
        }

        reset()
    }

    private fun listValues(values: List<Any>, appendor: String) {
        for ((localIndex, value) in values.withIndex()) {
            query.append("?")
            replacements[cursor] = value

            if (localIndex != (values.size - 1))
                query.append(appendor)

            cursor++
        }
    }

    private fun listElements(pairs: List<Pair<String, Any>>, appendor: String) {
        var localIndex = 0
        for ((key, value) in pairs) {
            query.append("$key = ?")
            replacements[cursor] = value

            if (localIndex != (pairs.size - 1))
                query.append(appendor)

            cursor++
            localIndex++
        }
    }

    private fun reset() {
        query.setLength(0)
        replacements.clear()
        cursor = 1
    }

    private fun getQuery(): String {
        val tempQuery = query.toString()
        reset()
        return tempQuery
    }

    fun print() {
        println()
        println("---- Replacements ----")
        for ((key, value) in replacements)
            println("$key -> $value")

        println("---- Query ----")
        println(getQuery())
        println()
    }

}
