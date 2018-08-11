package org.motivepick.web

import org.motivepick.domain.entity.User
import org.motivepick.domain.ui.user.CreateUserRequest
import org.motivepick.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
internal class UserController(private val repository: UserRepository) {

    @PostMapping
    fun create(@RequestBody request: CreateUserRequest): ResponseEntity<User> {
        return repository.findByAccountId(request.accountId)?.let {
            it.token = request.token

            return ok(repository.save(it))
        } ?: run {
            val user = User(request.accountId, request.name, request.token)
            return ok(repository.save(user))
        }
    }

    @GetMapping("/{accountId}")
    fun read(@PathVariable("accountId") accountId: Long): ResponseEntity<User> {
        return repository.findByAccountId(accountId)
                ?.let { ok(it) }
                ?: notFound().build()
    }

    @GetMapping
    fun readAll(): ResponseEntity<Iterable<User>> {
        return ok(repository.findAll())
    }

    // TODO: the method doesn't actually delete user, but does reset user's token
    @DeleteMapping("/{accountId}")
    fun delete(@PathVariable("accountId") accountId: Long): ResponseEntity<Any> {
        return repository.findByAccountId(accountId)?.let {
            it.token = null
            repository.save(it)

            return ok().build()
        } ?: notFound().build()
    }
}
