package org.motivepick

import org.dbunit.dataset.datatype.DataType
import org.dbunit.dataset.datatype.DefaultDataTypeFactory
import java.util.*

internal class H2DataTypeFactory : DefaultDataTypeFactory() {
    override fun createDataType(sqlType: Int, sqlTypeName: String, tableName: String?, columnName: String?): DataType? {
        return if (sqlType == 1111 && sqlTypeName.lowercase(Locale.getDefault()).startsWith("json")) {
            JsonDataType()
        } else {
            super.createDataType(sqlType, sqlTypeName, tableName, columnName)
        }
    }
}
