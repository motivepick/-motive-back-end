package org.motivepick.repository

import org.motivepick.domain.Task
import org.springframework.data.mongodb.repository.MongoRepository

interface TaskRepository : MongoRepository<Task, String>
