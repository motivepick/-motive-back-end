package org.motivepick.extensions

import org.motivepick.domain.entity.UserEntity
import org.motivepick.domain.view.UserView

internal object UserEntityExtensions {

    fun UserEntity.view(): UserView = UserView(this.accountId, this.name, this.temporary)
}
