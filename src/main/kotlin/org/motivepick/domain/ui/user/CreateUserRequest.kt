package org.motivepick.domain.ui.user

import com.fasterxml.jackson.annotation.JsonCreator

data class CreateUserRequest @JsonCreator constructor(
        val accountId: Long,
        val name: String,
        val token: String
)