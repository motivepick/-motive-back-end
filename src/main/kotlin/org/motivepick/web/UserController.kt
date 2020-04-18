package org.motivepick.web

import org.motivepick.domain.entity.User
import org.motivepick.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class UserController(private val userService: UserService) {

    @GetMapping("/user")
    fun read(): ResponseEntity<User> = userService.readCurrentUser()?.let { ok(it) } ?: notFound().build()
}
