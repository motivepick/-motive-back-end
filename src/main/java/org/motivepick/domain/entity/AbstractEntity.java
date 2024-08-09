package org.motivepick.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import static javax.persistence.GenerationType.IDENTITY;

@MappedSuperclass
abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id = null;
}
