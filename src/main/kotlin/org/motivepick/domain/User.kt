package org.motivepick.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class User {

    @Id
    var id: String? = null
    var name: String? = null
    var token: String? = null
}
