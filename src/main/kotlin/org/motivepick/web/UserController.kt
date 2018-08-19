package org.motivepick.web

import org.motivepick.domain.entity.User
import org.motivepick.repository.UserRepository
import org.motivepick.security.CurrentUser
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/users")
internal class UserController(
        private val repo: UserRepository,
        private val currentUser: CurrentUser) {

    @GetMapping
    fun read(): ResponseEntity<User> =
            repo.findByAccountId(currentUser.getAccountId())
                    ?.let { ok(it) }
                    ?: notFound().build()

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest) {
        request.getSession(false)?.invalidate()
        SecurityContextHolder.getContext().authentication = null
        SecurityContextHolder.clearContext()
    }
}
