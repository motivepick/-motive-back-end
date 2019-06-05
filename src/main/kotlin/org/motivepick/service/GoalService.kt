package org.motivepick.service

import org.motivepick.web.GoalDto
import java.util.*

interface GoalService {

    fun read(): List<GoalDto>

    fun read(id: Long): Optional<GoalDto>
}
