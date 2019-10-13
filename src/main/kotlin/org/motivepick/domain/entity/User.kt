package org.motivepick.domain.entity

import javax.persistence.Column
import javax.persistence.Entity

// user is a reserved keyword
@Entity(name = "USER_ACCOUNT")
class User(
        @Column(nullable = false)
        var accountId: String,

        @Column(nullable = false)
        var name: String,

        @Column(nullable = false)
        var temporary: Boolean) : AbstractEntity()
