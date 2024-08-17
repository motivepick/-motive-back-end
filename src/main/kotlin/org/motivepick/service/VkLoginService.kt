package org.motivepick.service

import org.motivepick.config.CookieFactory
import org.motivepick.config.ServerConfig
import org.motivepick.config.VkConfig
import org.motivepick.repository.LoginStateRepository
import org.motivepick.security.vk.VkService
import org.motivepick.security.vk.VkTokenResponse
import org.springframework.stereotype.Service

@Service
internal class VkLoginService(
    config: VkConfig,
    tokenGenerator: VkService,
    serverConfig: ServerConfig,
    cookieFactory: CookieFactory,
    loginStateRepository: LoginStateRepository
) : AbstractOauth2LoginService<VkTokenResponse>(config, tokenGenerator, serverConfig, cookieFactory, loginStateRepository)
