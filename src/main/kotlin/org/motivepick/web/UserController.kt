package org.motivepick.web

import org.motivepick.domain.entity.User
import org.motivepick.domain.ui.user.CreateUserRequest
import org.motivepick.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/users")
internal class UserController(private val repository: UserRepository,
        @Value("\${motive.facebook.client-secret}") private val clientSecret: String) {

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
    fun read(@PathVariable("accountId") accountId: Long): ResponseEntity<User> =
            repository.findByAccountId(accountId)
                    ?.let { ok(it) }
                    ?: notFound().build()

    @PostMapping("/codes/{code}")
    fun exchangeCodeForToken(@PathVariable("code") code: String, @RequestParam("redirectUrl") redirectUrl: String,
            @RequestParam("clientId") clientId: String): ResponseEntity<String> {
        val uri = UriComponentsBuilder.fromUriString("https://graph.facebook.com/v3.0/oauth/access_token")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .queryParam("redirect_uri", redirectUrl).build().toUri()
        val response = RestTemplate().getForObject(uri, TokenResponse::class.java)
        return response?.let { ok(it.token) } ?: ResponseEntity(INTERNAL_SERVER_ERROR)
    }

    @GetMapping
    fun readAll(): ResponseEntity<Iterable<User>> = ok(repository.findAll())

    // TODO: the method doesn't actually delete user, but does reset user's token
    @DeleteMapping("/{accountId}")
    fun revoke(@PathVariable("accountId") accountId: Long): ResponseEntity<Any> {
        return repository.findByAccountId(accountId)?.let {
            it.token = null
            repository.save(it)

            return ok().build()
        } ?: notFound().build()
    }
}
