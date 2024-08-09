package org.motivepick.domain.entity

import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null
}
