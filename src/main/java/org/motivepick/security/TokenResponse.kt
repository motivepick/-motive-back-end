package org.motivepick.security

import com.fasterxml.jackson.annotation.JsonProperty

class TokenResponse(
        @JsonProperty("access_token")
        val token: String,

        @JsonProperty("user_id")
        val id: String?
)
