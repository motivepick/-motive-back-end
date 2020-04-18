package org.motivepick.web

import org.motivepick.repository.TaskRepository
import org.motivepick.security.CurrentUser
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ScheduleController(private val currentUser: CurrentUser, private val taskRepository: TaskRepository,
        private val scheduleFactory: ScheduleFactory) {

    @GetMapping("/schedule")
    fun schedule(): ResponseEntity<Schedule> {
        val tasks = taskRepository.findAllByUserAccountIdAndClosedFalseAndDueDateNotNullAndVisibleTrue(currentUser.getAccountId())
        return ok(scheduleFactory.scheduleFor(tasks))
    }
}
