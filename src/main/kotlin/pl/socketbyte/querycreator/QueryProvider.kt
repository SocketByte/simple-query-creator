package pl.socketbyte.querycreator

import pl.socketbyte.wrapp.FieldInfo
import pl.socketbyte.wrapp.Wrapper
import pl.socketbyte.wrapp.WrapperFactory
import java.sql.Connection

open class QueryProvider<T> constructor(private val instance: T,
                                   private val bind: Class<out T>,
                                   private val connection: Connection? = null,
                                   private val connectionPool: Boolean = false) {

    companion object {
        private val factory: WrapperFactory = WrapperFactory()
    }

    private val builder: QueryBuilder<T> = QueryBuilder(bind)
    private val wrapp: Wrapper<T> = factory.write(instance)

    init {
        if (connection != null)
            builder.bind(connection, connectionPool)
    }

    fun builder(): QueryBuilder<T> {
        return builder
    }

    // WIP feature (it works, but only when you give an example instance, which is stupid for selecting :/)
    // That's limitation of Wrapp, maybe in the future I will rewrite Wrapp to allow non-instanced wrappers (no values)
    fun selectWhere(vararg pairs: Pair<String, Any>, elements: String = "*"): List<Any> {
        if (connection == null)
            throw NullPointerException("connection can not be null during select query")

        val builder = builder
                .select(elements)
                .from()
                .where(*pairs)

        val resultSet = builder.query()

        val list = mutableListOf<Any>()
        while (resultSet.next()) {
            var index = 0
            for ((key) in wrapp.fields) {
                wrapp.fields[key]?.type = resultSet.getObject(index)
                index++
            }
            list.add(factory.read(wrapp, instance)!!)
        }

        resultSet.close()
        if (connectionPool)
            resultSet.statement.connection.close()
        resultSet.statement.close()

        return list
    }

    fun insert(): QueryBuilder<T> {
        val array: MutableList<Any> = mutableListOf()

        var index = 0
        for ((_, value) in wrapp.fields) {
            if (isWrapperType(value.type))
                continue
            array.add(value.type)

            index++
        }

        return builder
                .insert()
                .values(*array.toTypedArray())
    }

    fun update(vararg pairs: Pair<String, Any>): QueryBuilder<T> {
        val array: MutableList<Pair<String, Any>> = mutableListOf()

        var index = 0
        for ((key, value) in wrapp.fields) {
            if (isWrapperType(value.type))
                continue
            array.add(key to value.type)

            index++
        }

        return builder
                .update()
                .set(*array.toTypedArray())
                .where(*pairs)
    }

    fun table(): QueryBuilder<T> {
        val array: MutableList<Pair<String, String>> = mutableListOf()

        var index = 0
        for ((key, value) in wrapp.fields) {
            if (isWrapperType(value.type))
                continue
            array.add(key to TypeResolver.resolve(value.typeClass))

            index++
        }

        return builder.table(*array.toTypedArray())
    }

    // Query provider does not support deep invoking
    private fun isWrapperType(type: Any): Boolean {
        return type is Wrapper<*>
    }

}
