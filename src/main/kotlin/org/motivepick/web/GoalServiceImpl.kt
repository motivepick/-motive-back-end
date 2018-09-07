package org.motivepick.web

import org.motivepick.domain.entity.Goal
import org.motivepick.domain.entity.Task
import org.motivepick.repository.GoalRepository
import org.motivepick.security.CurrentUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class GoalServiceImpl(private val currentUser: CurrentUser, private val repository: GoalRepository) : GoalService {

    @Transactional(readOnly = true)
    override fun read(): List<GoalDto> = repository.findAllByUserAccountId(currentUser.getAccountId()).map { map(it) }

    @Transactional(readOnly = true)
    override fun read(id: Long): Optional<GoalDto> = repository.findById(id).map { Optional.of(map(it)) }.orElse(Optional.empty())

    private fun map(it: Goal) =
            GoalDto(it.id, it.name, it.description, it.colorTag, it.created, it.dueDate, it.closed, open(it.tasks))

    private fun open(tasks: List<Task>): List<Task> = tasks.filter { !it.closed }
}
