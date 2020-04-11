package org.motivepick.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable


object Lists {

    fun <T> insertWithShift(list: List<T>, index: Int, element: T): List<T> =
            list.subList(0, index) + element + list.subList(index, list.size)

    fun <T> withPageable(list: List<T>, pageable: Pageable): Page<T> {
        val start = pageable.offset.toInt()
        val end = if (start + pageable.pageSize > list.size) list.size else start + pageable.pageSize
        return PageImpl(list.subList(start, end), pageable, list.size.toLong())
    }
}
