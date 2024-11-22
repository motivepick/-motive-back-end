package org.motivepick.web

import org.motivepick.domain.view.RescheduleTaskRequest
import org.motivepick.domain.view.ScheduledTaskView
import org.motivepick.service.TaskService
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import java.time.ZoneId

@RestController
internal class ScheduleController(private val taskService: TaskService) {

    @GetMapping("/schedule")
    fun schedule(@RequestParam(name = "timeZone", defaultValue = "UTC") timeZone: String): ResponseEntity<List<ScheduledTaskView>> =
        ok(taskService.findScheduleForCurrentUser(ZoneId.of(timeZone)))

    @PostMapping("/tasks/{id}/reschedule")
    fun schedule(@PathVariable("id") taskId: Long, @RequestBody request: RescheduleTaskRequest): ResponseEntity<ScheduledTaskView> =
        ok(taskService.rescheduleTask(taskId, request))
}
