package org.motivepick.web

import com.fasterxml.jackson.annotation.JsonProperty

internal class TokenResponse {

    @JsonProperty("access_token")
    var token: String = ""
}
