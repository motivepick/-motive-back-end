package org.motivepick.service

import org.motivepick.config.CookieFactory
import org.motivepick.config.FacebookConfig
import org.motivepick.config.ServerConfig
import org.motivepick.repository.LoginStateRepository
import org.motivepick.security.FacebookService
import org.springframework.stereotype.Service

@Service
class FacebookLoginService(
    config: FacebookConfig,
    tokenGenerator: FacebookService,
    serverConfig: ServerConfig,
    cookieFactory: CookieFactory,
    loginStateRepository: LoginStateRepository
) : AbstractOauth2LoginService(config, tokenGenerator, serverConfig, cookieFactory, loginStateRepository)
