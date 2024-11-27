package org.motivepick.service

import io.jsonwebtoken.lang.Assert
import org.motivepick.domain.entity.UserEntity
import org.motivepick.domain.view.UserView
import org.motivepick.extensions.UserEntityExtensions.view
import org.motivepick.repository.UserRepository
import org.motivepick.security.CurrentUser
import org.motivepick.security.JWT_TOKEN_COOKIE
import org.motivepick.security.OAuth2Profile
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class UserServiceImpl(private val user: CurrentUser, private val repository: UserRepository, private val taskService: TaskService) : UserService {

    private val logger: Logger = getLogger(UserServiceImpl::class.java)

    @Transactional
    override fun readCurrentUser(): UserView? = repository.findByAccountId(user.getAccountId())?.view()

    /**
     * Either `temporaryAccountId` or `oauth2Profile` is always present, depending on whether the user
     * that is trying to log in for the first time is temporary or permanent (from the OAuth2 service).
     * They may also both be present if a temporary user is promoting themselves to a permanent user
     * by logging in via the OAuth2 service. In this case, we need to migrate the tasks and delete the
     * temporary user.
     */
    @Transactional
    override fun createUserWithTasksIfNotExists(temporaryAccountId: String, oAuth2Profile: OAuth2Profile, language: String): UserView {
        Assert.isTrue(temporaryAccountId.isNotBlank() || oAuth2Profile.id.isNotBlank(), "Both temporaryAccountId and oauth2Profile.id are blank")
        if (temporaryAccountId.isBlank()) {
            return findOrCreateUserWithTasks(oAuth2Profile.id, oAuth2Profile.name, false, language).view()
        } else {
            val temporaryUser = findOrCreateUserWithTasks(temporaryAccountId, "", true, language)
            if (oAuth2Profile.id.isBlank()) {
                return temporaryUser.view()
            }

            if (isIndeedTemporary(temporaryUser)) {
                val permanentUser = findOrCreatePermanentUserWithoutTasks(oAuth2Profile.id, oAuth2Profile.name)
                taskService.migrateTasks(temporaryAccountId, permanentUser.accountId)
                return permanentUser.view()
            } else {
                logger.warn("User $temporaryAccountId is not temporary. Check that the $JWT_TOKEN_COOKIE cookie gets removed")
                return findOrCreateUserWithTasks(oAuth2Profile.id, oAuth2Profile.name, false, language).view()
            }
        }
    }

    @Transactional
    override fun deleteTemporaryUserWithTasks(accountId: String) {
        val user = repository.findByAccountId(accountId)
        if (user == null) {
            throw RuntimeException("Unexpected situation: user with ID $accountId should be in the database, but one is absent")
        } else if (isIndeedTemporary(user)) {
            logger.info("Going to delete temporary user {} with their tasks", accountId)
            taskService.deleteTasksFully(accountId)
            repository.delete(user)
        } else {
            logger.info("User $accountId is not temporary, no need to delete anything")
        }
    }

    private fun findOrCreatePermanentUserWithoutTasks(accountId: String, name: String): UserEntity =
        repository.findByAccountId(accountId) ?: newUserWithoutTasks(accountId, name, false)

    private fun findOrCreateUserWithTasks(accountId: String, name: String, temporary: Boolean, language: String): UserEntity =
        repository.findByAccountId(accountId) ?: newUserWithTasks(accountId, name, temporary, language)

    /**
     * Migrate tasks only if the source user is indeed temporary to prevent migration in case
     * when cookie removal does not work and a user has more than one account in Motive.
     */
    private fun isIndeedTemporary(user: UserEntity) = user.temporary

    private fun newUserWithoutTasks(accountId: String, name: String, temporary: Boolean): UserEntity =
        repository.save(UserEntity(accountId, name, temporary))

    private fun newUserWithTasks(accountId: String, name: String, temporary: Boolean, language: String): UserEntity {
        val user = newUserWithoutTasks(accountId, name, temporary)
        taskService.createInitialTasks(user, language)
        return user
    }
}
