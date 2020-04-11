package org.motivepick.web

import org.motivepick.domain.entity.Task
import org.motivepick.domain.entity.TaskListType
import org.motivepick.security.CurrentUser
import org.motivepick.service.TaskService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*

@RestController
class TaskListController(private val user: CurrentUser, private val taskService: TaskService) {

    @GetMapping("/task-lists/{type}")
    fun read(@PathVariable("type") listType: TaskListType, pageable: Pageable): ResponseEntity<Page<Task>> =
            ok(taskService.findForCurrentUser(listType, pageable))

    @PostMapping("/orders")
    fun moveTask(@RequestBody request: MoveTaskRequest): ResponseEntity<Void> {
        val accountId = user.getAccountId()
        // TODO
//        taskOrderService.moveTask(accountId, request.sourceId!!, request.destinationId!!)
        return ok().build()
    }

    // TODO: implement close task by moving task

    // TODO: task deletion as moving task as well
}
