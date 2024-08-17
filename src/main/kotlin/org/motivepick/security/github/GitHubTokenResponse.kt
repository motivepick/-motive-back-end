package org.motivepick.security.github

import com.fasterxml.jackson.annotation.JsonProperty

data class GitHubTokenResponse(

    @JsonProperty("access_token")
    val token: String
)
