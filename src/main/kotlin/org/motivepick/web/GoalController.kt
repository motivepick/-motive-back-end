package org.motivepick.web

import org.motivepick.domain.document.Goal
import org.motivepick.domain.ui.CreateGoalRequest
import org.motivepick.domain.ui.UpdateGoalRequest
import org.motivepick.repository.GoalRepository
import org.motivepick.repository.TaskRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/goals")
internal class GoalController(private val goalRepo: GoalRepository, private val taskRepo: TaskRepository) {

    @PostMapping()
    fun create(@RequestBody request: CreateGoalRequest): ResponseEntity<Goal> {
        val goal = Goal()
        goal.userId = request.userId
        goal.name = request.name
        goal.description = request.description
        goal.dueDate = request.dueDate
        goalRepo.insert(goal)
        return ResponseEntity(goal, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun read(@PathVariable("id") goalId: String): ResponseEntity<Goal> {
        return goalRepo.findById(goalId)
                .map { ResponseEntity.ok(it) }
                .orElse(ResponseEntity.notFound().build())
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") goalId: String, @RequestBody request: UpdateGoalRequest): ResponseEntity<Goal> {
        return goalRepo.findById(goalId)
                .map { goal ->
                    request.name?.let { goal.name = it }
                    request.description?.let { goal.description = it }
                    request.dueDate?.let { goal.dueDate = it }
                    request.closed?.let { goal.closed = it }
                    return@map ResponseEntity.ok(goalRepo.save(goal))
                }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") goalId: String): ResponseEntity<Any> {
        if (goalRepo.existsById(goalId)) {
            goalRepo.deleteById(goalId)
            return ResponseEntity(HttpStatus.OK)
        } else {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/{id}/assign-task")
    fun assign(@PathVariable("id") goalId: String, @RequestParam("taskId") taskId: String): ResponseEntity<Any> {
        return goalRepo.findById(goalId).map { goal ->
            taskRepo.findById(taskId).map { task ->
                goal.tasks.add(task)
                goalRepo.save(goal)

                task.goal = goal
                taskRepo.save(task)

                ResponseEntity<Any>(HttpStatus.OK)
            }.orElse(ResponseEntity(HttpStatus.NOT_FOUND))
        }.orElse(ResponseEntity(HttpStatus.NOT_FOUND))
    }
}
