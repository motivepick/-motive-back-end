package org.motivepick.repository

import org.motivepick.domain.entity.Goal
import org.springframework.data.repository.PagingAndSortingRepository

interface GoalRepository : PagingAndSortingRepository<Goal, Long>