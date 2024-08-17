package org.motivepick.service

import org.motivepick.config.CookieFactory
import org.motivepick.config.GitHubConfig
import org.motivepick.config.ServerConfig
import org.motivepick.repository.LoginStateRepository
import org.motivepick.security.github.GitHubService
import org.motivepick.security.github.GitHubTokenResponse
import org.springframework.stereotype.Service

@Service
internal class GitHubLoginService(
    config: GitHubConfig,
    tokenGenerator: GitHubService,
    serverConfig: ServerConfig,
    cookieFactory: CookieFactory,
    loginStateRepository: LoginStateRepository
) : AbstractOauth2LoginService<GitHubTokenResponse>(config, tokenGenerator, serverConfig, cookieFactory, loginStateRepository)
