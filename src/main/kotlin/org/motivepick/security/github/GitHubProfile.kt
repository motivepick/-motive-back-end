package org.motivepick.security.github

import com.fasterxml.jackson.annotation.JsonProperty

data class GitHubProfile(

    @JsonProperty("id")
    val id: Int,

    @JsonProperty("login")
    val login: String
)
