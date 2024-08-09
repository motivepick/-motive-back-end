package org.motivepick.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity

// user is a reserved keyword
@Entity(name = "USER_ACCOUNT")
class UserEntity(
        @Column(nullable = false)
        var accountId: String,

        @Column(nullable = false)
        var name: String,

        @Column(nullable = false)
        var temporary: Boolean) : AbstractEntity() {

        constructor() : this("", "", false)
}
