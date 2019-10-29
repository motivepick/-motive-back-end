package org.motivepick.service

import org.motivepick.domain.entity.User
import org.motivepick.repository.UserRepository
import org.motivepick.security.CurrentUser
import org.motivepick.security.JWT_TOKEN_COOKIE
import org.motivepick.security.Profile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(private val user: CurrentUser, private val repository: UserRepository, private val taskService: TaskService) : UserService {

    val logger: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    @Transactional
    override fun readCurrentUser(): User? = repository.findByAccountId(user.getAccountId())

    @Transactional
    override fun createUserWithTasksIfNotExists(profile: Profile): User {
        return try {
            val temporaryAccountId = user.getAccountId()
            val temporaryUser = repository.findByAccountId(temporaryAccountId)
            if (temporaryUser == null) {
                throw RuntimeException("Unexpected situation: temporary user with ID "
                        + temporaryAccountId + " should be in the database, but one is absent")
            } else {
                val existingUser = repository.findByAccountId(profile.id)
                if (existingUser == null) {
                    temporaryUser.accountId = profile.id
                    temporaryUser.temporary = false
                    temporaryUser.name = profile.name
                    repository.save(temporaryUser)
                } else {
                    if (isIndeedTemporary(temporaryUser)) {
                        taskService.migrateTasks(temporaryAccountId, profile.id)
                    } else {
                        logger.warn("User $temporaryAccountId is not temporary. Check that the $JWT_TOKEN_COOKIE cookie gets removed")
                    }
                    existingUser
                }
            }
        } catch (e: UsernameNotFoundException) {
            repository.findByAccountId(profile.id) ?: newUserWithTasks(profile.id, profile.name, profile.temporary)
        }
    }

    /**
     * Migrate tasks only if the source user is indeed temporary to prevent migration in case
     * when cookie removal does not work and a user has more than one account in Motive.
     */
    private fun isIndeedTemporary(source: User) = source.temporary

    private fun newUserWithTasks(accountId: String, name: String, temporary: Boolean): User {
        val user = repository.save(User(accountId, name, temporary))
        taskService.createInitialTasks(user)
        return user
    }
}