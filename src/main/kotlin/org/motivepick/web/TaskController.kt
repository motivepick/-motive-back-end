package org.motivepick.web

import org.motivepick.domain.entity.Task
import org.motivepick.domain.ui.task.CreateTaskRequest
import org.motivepick.domain.ui.task.UpdateTaskRequest
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.UserRepository
import org.motivepick.service.TaskListService
import org.motivepick.service.TaskService
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tasks")
internal class TaskController(private val taskRepo: TaskRepository, private val userRepo: UserRepository,
        private val taskService: TaskService, private val taskListService: TaskListService) {

    @PostMapping
    fun create(@RequestBody request: CreateTaskRequest): ResponseEntity<Task> =
            ResponseEntity(taskService.createTask(request), CREATED)

    @GetMapping("/{id}")
    fun read(@PathVariable("id") taskId: Long): ResponseEntity<Task> =
            taskRepo.findByIdAndVisibleTrue(taskId)
                    .map { ok(it) }
                    .orElse(notFound().build())

    @PutMapping("/{id}")
    fun update(@PathVariable("id") taskId: Long, @RequestBody request: UpdateTaskRequest): ResponseEntity<Task> =
            taskRepo.findByIdAndVisibleTrue(taskId)
                    .map { task ->
                        request.name?.let { task.name = it.trim() }
                        request.description?.let { task.description = it.trim() }
                        request.created?.let { task.created = it }
                        request.dueDate?.let { task.dueDate = it }
                        request.closingDate?.let { task.closingDate = it }
                        request.closed?.let { task.closed = it }
                        if (request.deleteDueDate) {
                            task.dueDate = null
                        }
                        ok(taskRepo.save(task))
                    }
                    .orElse(notFound().build())

    @PutMapping("/{id}/closing")
    fun close(@PathVariable("id") taskId: Long): ResponseEntity<Task> =
            taskListService.closeTask(taskId)
                    .map { ok(it) }
                    .orElse(notFound().build())

    @PutMapping("/{id}/undo-closing")
    fun undoClose(@PathVariable("id") taskId: Long): ResponseEntity<Task> =
            taskListService.undoCloseTask(taskId)
                    .map { ok(it) }
                    .orElse(notFound().build())

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") taskId: Long): ResponseEntity<Task> =
            taskRepo.findByIdAndVisibleTrue(taskId)
                    .map { task ->
                        task.visible = false
                        ok(taskRepo.save(task))
                    }
                    .orElse(ResponseEntity(NOT_FOUND))
}
