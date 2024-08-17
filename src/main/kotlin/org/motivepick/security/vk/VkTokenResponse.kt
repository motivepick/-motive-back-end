package org.motivepick.security.vk

import com.fasterxml.jackson.annotation.JsonProperty

class VkTokenResponse(

    @JsonProperty("access_token")
    val token: String,

    @JsonProperty("user_id")
    val id: String?
)
