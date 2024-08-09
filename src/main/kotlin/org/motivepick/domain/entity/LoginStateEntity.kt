package org.motivepick.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity(name = "LOGIN_STATE")
class LoginStateEntity(
    @Column(nullable = false)
    var stateUuid: String,

    @Column(nullable = false)
    var mobile: Boolean
) : AbstractEntity() {

    constructor() : this("", false)
}
