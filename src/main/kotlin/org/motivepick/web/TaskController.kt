package org.motivepick.web

import org.motivepick.domain.entity.Task
import org.motivepick.domain.ui.task.CreateTaskRequest
import org.motivepick.domain.ui.task.UpdateTaskRequest
import org.motivepick.extension.getAccountId
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.UserRepository
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tasks")
internal class TaskController(private val taskRepo: TaskRepository, private val userRepo: UserRepository) {

    @PostMapping
    fun create(authentication: OAuth2AuthenticationToken, @RequestBody request: CreateTaskRequest): ResponseEntity<Task> {
        return userRepo.findByAccountId(authentication.getAccountId())?.let { user ->
            val task = Task(user, request.name)
            task.description = request.description
            task.dueDate = request.dueDate

            return ResponseEntity(taskRepo.save(task), CREATED)
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/list")
    fun list(authentication: OAuth2AuthenticationToken,
             @RequestParam(name = "onlyOpen", defaultValue = "true") onlyOpen: Boolean): ResponseEntity<List<Task>> {
        return ok(taskRepo.findAllByUserAccountIdAndClosedOrderByCreatedDesc(authentication.getAccountId(), !onlyOpen))
    }

    @GetMapping("/{id}")
    fun read(@PathVariable("id") taskId: Long): ResponseEntity<Task> =
            taskRepo.findById(taskId)
                    .map { ok(it) }
                    .orElse(notFound().build())

    @PutMapping("/{id}")
    fun update(@PathVariable("id") taskId: Long, @RequestBody request: UpdateTaskRequest): ResponseEntity<Task> {
        return taskRepo.findById(taskId)
                .map { task ->
                    request.name?.let { task.name = it }
                    request.description?.let { task.description = it }
                    request.dueDate?.let { task.dueDate = it }
                    request.closed?.let { task.closed = it }
                    return@map ResponseEntity.ok(taskRepo.save(task))
                }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") taskId: Long): ResponseEntity<Any> {
        return if (taskRepo.existsById(taskId)) {
            taskRepo.deleteById(taskId)
            ResponseEntity(OK)
        } else {
            ResponseEntity(NOT_FOUND)
        }
    }
}
