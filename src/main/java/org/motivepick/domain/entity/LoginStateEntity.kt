package org.motivepick.domain.entity

import javax.persistence.Column
import javax.persistence.Entity

@Entity(name = "LOGIN_STATE")
class LoginStateEntity(
    @Column(nullable = false)
    var stateUuid: String,

    @Column(nullable = false)
    var mobile: Boolean
) : AbstractEntity()
