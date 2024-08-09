package org.motivepick

import org.dbunit.dataset.datatype.AbstractDataType
import org.h2.value.ValueJson
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

internal class JsonDataType : AbstractDataType("json", Types.OTHER, String::class.java, false) {

    override fun typeCast(value: Any): Any {
        return value.toString()
    }

    override fun getSqlValue(column: Int, resultSet: ResultSet): Any {
        return resultSet.getString(column)
    }

    override fun setSqlValue(value: Any, column: Int, statement: PreparedStatement) {
        statement.setObject(column, ValueJson.fromJson(value.toString()))
    }
}
