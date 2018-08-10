package org.motivepick.domain.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
open class User {

    @Id
    var id: String? = null
    var name: String? = null
    var token: String? = null
}
