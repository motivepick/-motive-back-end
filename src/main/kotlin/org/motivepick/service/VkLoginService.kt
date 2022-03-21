package org.motivepick.service

import org.motivepick.config.CookieFactory
import org.motivepick.config.ServerConfig
import org.motivepick.config.VkConfig
import org.motivepick.repository.LoginStateRepository
import org.motivepick.security.VkService
import org.springframework.stereotype.Service

@Service
class VkLoginService(
    config: VkConfig,
    tokenGenerator: VkService,
    serverConfig: ServerConfig,
    cookieFactory: CookieFactory,
    loginStateRepository: LoginStateRepository
) : AbstractOauth2LoginService(config, tokenGenerator, serverConfig, cookieFactory, loginStateRepository)
