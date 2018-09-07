package org.motivepick.web

import java.util.*

interface GoalService {

    fun read(): List<GoalDto>

    fun read(id: Long): Optional<GoalDto>
}
