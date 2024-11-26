package org.motivepick.service

import org.motivepick.config.CookieFactory
import org.motivepick.config.ServerConfig
import org.motivepick.config.VkConfig
import org.motivepick.repository.LoginStateRepository
import org.motivepick.security.JwtTokenService
import org.motivepick.security.vk.VkClient
import org.motivepick.security.vk.VkTokenResponse
import org.springframework.stereotype.Service

@Service
internal class VkLoginService(
    config: VkConfig,
    tokenGenerator: VkClient,
    serverConfig: ServerConfig,
    cookieFactory: CookieFactory,
    loginStateRepository: LoginStateRepository,
    userService: UserService,
    tokenService: JwtTokenService
) : AbstractOauth2LoginService<VkTokenResponse>(config, tokenGenerator, serverConfig, cookieFactory, loginStateRepository, userService, tokenService)
