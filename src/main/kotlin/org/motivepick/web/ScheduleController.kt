package org.motivepick.web

import org.motivepick.domain.model.Schedule
import org.motivepick.service.TaskService
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ScheduleController(private val taskService: TaskService) {

    @GetMapping("/schedule")
    fun schedule(): ResponseEntity<Schedule> = ok(taskService.findScheduleForCurrentUser())
}
