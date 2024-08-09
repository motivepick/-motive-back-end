package org.motivepick.service;

import org.motivepick.domain.entity.UserEntity;
import org.motivepick.security.Profile;

public interface UserService {

    UserEntity readCurrentUser();

    UserEntity createUserWithTasksIfNotExists(Profile profile, String language);

    void deleteTemporaryUserWithTasks(String accountId);
}
