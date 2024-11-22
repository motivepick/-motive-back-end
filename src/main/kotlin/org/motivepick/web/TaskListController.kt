package org.motivepick.web

import org.motivepick.domain.view.MoveTaskRequest
import org.motivepick.domain.view.TaskListView
import org.motivepick.domain.view.TaskView
import org.motivepick.extensions.TaskListExtensions.view
import org.motivepick.service.TaskListService
import org.motivepick.service.TaskService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*

@RestController
internal class TaskListController(private val taskService: TaskService, private val taskListService: TaskListService) {

    @PostMapping("/task-lists")
    fun create(): ResponseEntity<TaskListView> =
        ok(taskListService.createTaskList().view())

    @GetMapping("/task-lists/{id}")
    fun read(@PathVariable("id") listId: String, @RequestParam("offset") offset: Long, @RequestParam("limit") limit: Int): ResponseEntity<Page<TaskView>> =
        ok(taskService.findForCurrentUser(listId, offset, limit))

    @PostMapping("/orders")
    fun moveTask(@RequestBody request: MoveTaskRequest): ResponseEntity<TaskView> =
        ok(taskListService.moveTask(request.sourceListId!!, request.taskId!!, request.destinationListId!!, request.destinationIndex!!))
}
