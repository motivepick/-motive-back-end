package org.motivepick.extensions

import org.motivepick.domain.entity.TaskEntity
import org.motivepick.security.CurrentUser

internal object CurrentUserExtensions {

    internal fun CurrentUser.owns(task: TaskEntity): Boolean =
        task.user.accountId == this.getAccountId()
}
