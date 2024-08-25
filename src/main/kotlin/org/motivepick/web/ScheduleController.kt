package org.motivepick.web

import org.motivepick.domain.view.ScheduleView
import org.motivepick.service.TaskService
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.ZoneId

@RestController
internal class ScheduleController(private val taskService: TaskService) {

    @GetMapping("/schedule")
    fun schedule(timeZone: ZoneId?): ResponseEntity<ScheduleView> = ok(taskService.findScheduleForCurrentUser(timeZone ?: ZoneId.of("UTC")))
}
