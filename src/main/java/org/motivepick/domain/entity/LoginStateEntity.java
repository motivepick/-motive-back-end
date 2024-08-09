package org.motivepick.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "LOGIN_STATE")
public
class LoginStateEntity extends AbstractEntity {
    @Column(nullable = false)
    String stateUuid;

    @Column(nullable = false)
    boolean mobile;
}
