package org.motivepick.security.vk

import com.fasterxml.jackson.annotation.JsonProperty

data class VkProfileResponse(

    @JsonProperty("response")
    val profiles: List<VkProfile>
)
