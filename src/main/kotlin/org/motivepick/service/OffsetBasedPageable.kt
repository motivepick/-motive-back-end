package org.motivepick.service

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

@Suppress("DataClassPrivateConstructor") // Kotlin issue, will be fixed, see KT-11914
internal data class OffsetBasedPageable private constructor(private val offset: Long, private val limit: Int, private val sort: Sort) : Pageable {

    companion object {

        /**
         * Creates a new unsorted [OffsetBasedPageable].
         *
         * @param offset zero-based offset.
         * @param limit  the size of the elements to be returned.
         */
        operator fun invoke(offset: Long, limit: Int): OffsetBasedPageable {
            require(offset >= 0) { "Offset must not be less than zero" }
            require(limit >= 1) { "Limit must not be less than one" }
            return OffsetBasedPageable(offset, limit, Sort.unsorted())
        }
    }

    override fun getPageNumber(): Int = (offset / limit).toInt()

    override fun getPageSize(): Int = limit

    override fun getOffset(): Long = offset

    override fun getSort(): Sort = sort

    override fun next(): Pageable =
        OffsetBasedPageable(getOffset() + pageSize, pageSize, getSort())

    override fun previousOrFirst(): Pageable =
        if (hasPrevious()) OffsetBasedPageable(getOffset() - pageSize, pageSize, getSort()) else first()

    override fun first(): Pageable =
        OffsetBasedPageable(0, pageSize, getSort())

    override fun withPage(pageNumber: Int): Pageable =
        OffsetBasedPageable(getOffset() + pageNumber * pageSize, pageSize, getSort())

    override fun hasPrevious(): Boolean = offset > limit
}
