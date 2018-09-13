package org.motivepick.web

import org.motivepick.domain.entity.User
import org.motivepick.repository.UserRepository
import org.motivepick.security.CurrentUser
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}
