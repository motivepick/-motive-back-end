package org.motivepick.service

import org.motivepick.domain.entity.User
import org.motivepick.repository.UserRepository
import org.motivepick.security.CurrentUser
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserService(private val currentUser: CurrentUser, private val userRepo: UserRepository, private val taskService: TaskService) {

    fun readCurrentUser(): User? = userRepo.findByAccountId(currentUser.getAccountId())

    fun createUserWithTasksIfNotExists(accountId: String, name: String, temporary: Boolean): User {
        return try {
            val temporaryAccountId = currentUser.getAccountId()
            val temporaryUser = userRepo.findByAccountId(temporaryAccountId)
            if (temporaryUser == null) {
                throw RuntimeException("Unexpected situation: temporary user with ID "
                        + temporaryAccountId + " should be in the database, but one is absent")
            } else {
                val existingUser = userRepo.findByAccountId(accountId)
                if (existingUser == null) {
                    temporaryUser.accountId = accountId
                    temporaryUser.temporary = false
                    temporaryUser.name = name
                    userRepo.save(temporaryUser)
                } else {
                    taskService.migrateTasks(temporaryAccountId, accountId)
                    existingUser
                }
            }
        } catch (e: UsernameNotFoundException) {
            userRepo.findByAccountId(accountId) ?: newUserWithTasks(accountId, name, temporary)
        }
    }

    private fun newUserWithTasks(accountId: String, name: String, temporary: Boolean): User {
        val user = userRepo.save(User(accountId, name, temporary))
        taskService.createInitialTasks(user)
        return user
    }
}
