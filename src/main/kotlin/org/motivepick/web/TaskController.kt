package org.motivepick.web

import org.motivepick.domain.Task
import org.motivepick.repository.TaskRepository
import org.springframework.data.domain.Example
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.data.domain.Sort.Order
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime.now
import java.time.ZoneOffset.UTC

@RestController
internal class TaskController(private val repo: TaskRepository) {

    companion object {
        private val NEWEST_FIRST = Sort.by(Order(DESC, "instantOfCreation"))
    }

    @GetMapping("/users/{userId}/tasks")
    fun listTasks(@PathVariable("userId") userId: String,
                  @RequestParam(name = "onlyOpen", defaultValue = "true") onlyOpen: Boolean): ResponseEntity<List<Task>> {
        val probe = Task()
        probe.userId = userId
        if (onlyOpen) {
            probe.closed = false
        }
        return ok(repo.findAll(Example.of(probe), NEWEST_FIRST))
    }

    @GetMapping("/tasks/{id}")
    fun getTask(@PathVariable("id") taskId: String): ResponseEntity<Task> {
        return repo.findById(taskId)
                .map { ok(it) }
                .orElse(notFound().build())
    }

    @PostMapping("/tasks")
    fun createTask(@RequestBody task: Task): ResponseEntity<Task> {
        task.instantOfCreation = now(UTC)
        repo.insert(task)
        return ResponseEntity(task, CREATED)
    }

    @PutMapping("/tasks/{id}")
    fun updateTask(@PathVariable("id") taskId: String, @RequestBody newTask: Task): ResponseEntity<Task> {
        return repo.findById(taskId)
                .map { ok(save(it, newTask)) }
                .orElse(notFound().build())
    }

    @PostMapping("/closed-tasks/{id}")
    fun closeTask(@PathVariable("id") taskId: String): ResponseEntity<Task> {
        return repo.findById(taskId)
                .map { ok(close(it)) }
                .orElse(notFound().build())
    }

    @DeleteMapping("/tasks/{id}")
    fun deleteTask(@PathVariable("id") taskId: String): ResponseEntity<Any> {
        if (repo.existsById(taskId)) {
            repo.deleteById(taskId)
            return ResponseEntity(OK)
        } else {
            return ResponseEntity(NOT_FOUND)
        }
    }

    private fun save(task: Task, newTask: Task): Task {
        newTask.name?.let { task.name = it }
        newTask.description?.let { task.description = it }
        newTask.dueDate?.let { task.dueDate = it }
        return repo.save(task)
    }

    private fun close(task: Task): Task {
        task.closed = true
        return repo.save(task)
    }
}
