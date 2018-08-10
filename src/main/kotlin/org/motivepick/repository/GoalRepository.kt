package org.motivepick.repository

import org.motivepick.domain.document.Goal
import org.springframework.data.mongodb.repository.MongoRepository

interface GoalRepository : MongoRepository<Goal, String>
