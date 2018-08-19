package org.motivepick.web

import org.motivepick.domain.entity.Goal
import org.motivepick.domain.entity.Task
import org.motivepick.domain.ui.goal.CreateGoalRequest
import org.motivepick.domain.ui.goal.UpdateGoalRequest
import org.motivepick.extension.getAccountId
import org.motivepick.repository.GoalRepository
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/goals")
internal class GoalController
(private val goalRepo: GoalRepository, private val taskRepo: TaskRepository, private val userRepo: UserRepository) {

    @PostMapping
    fun create(authentication: OAuth2AuthenticationToken, @RequestBody request: CreateGoalRequest): ResponseEntity<Goal> {
        return userRepo.findByAccountId(authentication.getAccountId())?.let { user ->
            val goal = Goal(user, request.name)
            goal.description = request.description
            goal.dueDate = request.dueDate
            goal.colorTag = request.colorTag

            return ResponseEntity(goalRepo.save(goal), HttpStatus.CREATED)
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/list")
    fun list(authentication: OAuth2AuthenticationToken): ResponseEntity<List<Goal>> =
            ok(goalRepo.findAllByUserAccountId(authentication.getAccountId()))

    @GetMapping("/{id}/tasks")
    fun listTasks(@PathVariable("id") goalId: Long): ResponseEntity<List<Task>> =
            ok(taskRepo.findAllByGoalId(goalId))

    @GetMapping("/{id}")
    fun read(@PathVariable("id") goalId: Long): ResponseEntity<Goal> =
            goalRepo.findById(goalId)
                    .map { ResponseEntity.ok(it) }
                    .orElse(ResponseEntity.notFound().build())

    @PutMapping("/{id}")
    fun update(@PathVariable("id") goalId: Long, @RequestBody request: UpdateGoalRequest): ResponseEntity<Goal> {
        return goalRepo.findById(goalId)
                .map { goal ->
                    request.name?.let { goal.name = it }
                    request.description?.let { goal.description = it }
                    request.dueDate?.let { goal.dueDate = it }
                    request.closed?.let { goal.closed = it }
                    request.colorTag?.let { goal.colorTag = it }
                    return@map ResponseEntity.ok(goalRepo.save(goal))
                }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") goalId: Long): ResponseEntity<Any> =
            if (goalRepo.existsById(goalId)) {
                goalRepo.deleteById(goalId)
                ResponseEntity(HttpStatus.OK)
            } else {
                ResponseEntity(HttpStatus.NOT_FOUND)
            }

    @PutMapping("/{id}/assign-task")
    fun assign(@PathVariable("id") goalId: Long, @RequestParam("taskId") taskId: Long): ResponseEntity<Any> {
        return goalRepo.findById(goalId).map { goal ->
            taskRepo.findById(taskId).map { task ->
                goal.addTask(task)
                goalRepo.save(goal)

                ResponseEntity<Any>(HttpStatus.OK)
            }.orElse(ResponseEntity(HttpStatus.NOT_FOUND))
        }.orElse(ResponseEntity(HttpStatus.NOT_FOUND))
    }
}
