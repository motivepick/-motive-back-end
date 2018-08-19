package org.motivepick.web

import org.motivepick.domain.entity.User
import org.motivepick.extension.getAccountId
import org.motivepick.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/users")
internal class UserController(private val repo: UserRepository) {


    @GetMapping
    fun read(authenticationToken: OAuth2AuthenticationToken): ResponseEntity<User> =
            repo.findByAccountId(authenticationToken.getAccountId())
                    ?.let { ok(it) }
                    ?: notFound().build()

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest) {
        request.getSession(false)?.invalidate()
        SecurityContextHolder.getContext().authentication = null
        SecurityContextHolder.clearContext()
    }
}
