package org.motivepick.web

import org.motivepick.domain.entity.Task
import org.motivepick.domain.ui.task.CreateTaskRequest
import org.motivepick.domain.ui.task.UpdateTaskRequest
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.UserRepository
import org.motivepick.security.CurrentUser
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tasks")
internal class TaskController(
        private val taskRepo: TaskRepository,
        private val userRepo: UserRepository,
        private val statistician: Statistician,
        private val currentUser: CurrentUser) {

    @PostMapping
    fun create(@RequestBody request: CreateTaskRequest): ResponseEntity<Task> {
        val user = userRepo.findByAccountId(currentUser.getAccountId())!!
        val task = Task(user, request.name)
        task.description = request.description
        task.dueDate = request.dueDate

        return ResponseEntity(taskRepo.save(task), CREATED)
    }

    @GetMapping
    fun list(@RequestParam(name = "closed") closed: Boolean?): ResponseEntity<List<Task>> {
        val accountId = currentUser.getAccountId()
        return when {
            closed == null -> {
                ok(taskRepo.findAllByUserAccountIdOrderByCreatedDesc(accountId))
            }
            closed -> ok(taskRepo.findAllByUserAccountIdAndClosedTrueOrderByClosingDateDesc(accountId))
            else -> ok(taskRepo.findAllByUserAccountIdAndClosedFalseOrderByCreatedDesc(accountId))
        }
    }

    @GetMapping("/{id}")
    fun read(@PathVariable("id") taskId: Long): ResponseEntity<Task> =
            taskRepo.findById(taskId)
                    .map { ok(it) }
                    .orElse(notFound().build())

    @GetMapping("/statistics")
    fun statistics(): ResponseEntity<Statistics> {
        val tasks = taskRepo.findAllByUserAccountId(currentUser.getAccountId())
        return ok(statistician.calculateStatisticsFor(tasks))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") taskId: Long, @RequestBody request: UpdateTaskRequest): ResponseEntity<Task> =
            taskRepo.findById(taskId)
                    .map { task ->
                        request.name?.let { task.name = it }
                        request.description?.let { task.description = it }
                        request.created?.let { task.created = it }
                        request.dueDate?.let { task.dueDate = it }
                        request.closingDate?.let { task.closingDate = it }
                        request.closed?.let { task.closed = it }
                        if (request.deleteDueDate) {
                            task.dueDate = null
                        }
                        return@map ResponseEntity.ok(taskRepo.save(task))
                    }.orElse(ResponseEntity.notFound().build())

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") taskId: Long): ResponseEntity<Any> =
            if (taskRepo.existsById(taskId)) {
                taskRepo.deleteById(taskId)
                ResponseEntity(OK)
            } else {
                ResponseEntity(NOT_FOUND)
            }
}
