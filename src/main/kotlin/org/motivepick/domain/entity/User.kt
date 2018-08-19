package org.motivepick.domain.entity

import javax.persistence.Column
import javax.persistence.Entity

// user is a reserved keyword
@Entity(name = "USER_ACCOUNT")
class User(
        @Column(nullable = false)
        var accountId: Long,

        @Column(nullable = false)
        var name: String) : AbstractEntity() {

        // TODO: do we still need this
        @Column(nullable = true)
        var token: String? = null
}
