package org.motivepick.domain.entity

import javax.persistence.Column
import javax.persistence.Entity

// user is a reserved keyword
@Entity(name = "USER_ACCOUNT")
class User(
        @Column(nullable = false)
        var accountId: Long,

        @Column(nullable = false)
        var name: String) : AbstractEntity()

