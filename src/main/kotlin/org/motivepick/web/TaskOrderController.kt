package org.motivepick.web

import org.motivepick.security.CurrentUser
import org.motivepick.service.TasksOrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class TaskOrderController(private val currentUser: CurrentUser, private val tasksOrderService: TasksOrderService) {

    @PostMapping
    fun moveTask(@RequestBody request: MoveTaskRequest): ResponseEntity<Void> {
        val accountId = currentUser.getAccountId()
        tasksOrderService.moveTask(accountId, request.sourceId!!, request.destinationId!!)
        return ResponseEntity.ok().build()
    }
}
