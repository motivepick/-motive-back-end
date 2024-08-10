package org.motivepick.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

internal object ListExtensions {

    /**
     * Add an element to a specific position in a list. All elements that come after the position shift to the right.
     */
    internal fun <T> List<T>.add(index: Int, element: T): List<T> =
        this.take(index) + element + this.drop(index)

    internal fun <T> List<T>.withPageable(pageable: Pageable): Page<T> {
        val start = pageable.offset.toInt()
        val end = if (start + pageable.pageSize > this.size) this.size else start + pageable.pageSize
        return PageImpl(this.subList(start, end), pageable, this.size.toLong())
    }
}
