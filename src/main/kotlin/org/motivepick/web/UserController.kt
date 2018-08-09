package org.motivepick.web

import org.motivepick.domain.User
import org.motivepick.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
internal class UserController(private val repository: UserRepository) {

    @PostMapping("/{id}")
    fun create(@PathVariable("id") id: String, @RequestBody user: User): ResponseEntity<User> {
        user.id = id
        return if (repository.existsById(id)) ok(user) else ok(repository.insert(user))
    }

    @GetMapping("/{id}")
    fun read(@PathVariable("id") id: String): ResponseEntity<User> {
        return repository.findById(id)
                .map { ok(it) }
                .orElse(notFound().build())
    }

    @GetMapping
    fun readAll(): ResponseEntity<List<User>> {
        return ok(repository.findAll())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: String): ResponseEntity<User> {
        repository.deleteById(id)
        return ok().build()
    }
}
