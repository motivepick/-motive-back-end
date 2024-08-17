package org.motivepick.security.github

import com.fasterxml.jackson.annotation.JsonProperty

class GitHubTokenResponse(

    @JsonProperty("access_token")
    val token: String
)
