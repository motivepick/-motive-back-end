package org.motivepick.service

import org.motivepick.domain.entity.UserEntity
import org.motivepick.domain.view.UserView

object UserEntityExtensions {

    fun UserEntity.view(): UserView = UserView(this.accountId, this.name, this.temporary)
}
