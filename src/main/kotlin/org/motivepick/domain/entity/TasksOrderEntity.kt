package org.motivepick.domain.entity

import com.vladmihalcea.hibernate.type.array.ListArrayType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.LAZY
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "TASKS_ORDER")
@TypeDef(name = "ListArray", typeClass = ListArrayType::class)
class TasksOrderEntity(@ManyToOne(fetch = LAZY) @JoinColumn(name = "USER_ID", nullable = false) var user: User,
        @Type(type = "ListArray") @Column(nullable = false, columnDefinition = "BIGINT[]") var orderedIds: List<Long?>) : AbstractEntity()