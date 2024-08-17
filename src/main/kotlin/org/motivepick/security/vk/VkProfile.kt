package org.motivepick.security.vk

import com.fasterxml.jackson.annotation.JsonProperty

data class VkProfile(

    @JsonProperty("first_name")
    val firstName: String,

    @JsonProperty("last_name")
    val lastName: String
)
