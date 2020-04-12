package org.motivepick.service

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.io.Serializable
import java.util.*

class OffsetBasedPageRequest(offset: Long, limit: Int, sort: Sort) : Pageable, Serializable {

    private val limit: Int
    private val offset: Long
    private val sort: Sort

    /**
     * Creates a new [OffsetBasedPageRequest] with sort parameters applied.
     *
     * @param offset     zero-based offset.
     * @param limit      the size of the elements to be returned.
     * @param direction  the direction of the [Sort] to be specified, can be null.
     * @param properties the properties to sort by, must not be null or empty.
     */
    constructor(offset: Int, limit: Int, direction: Sort.Direction?, vararg properties: String?) : this(offset.toLong(), limit, Sort(direction, *properties)) {}

    /**
     * Creates a new [OffsetBasedPageRequest] with sort parameters applied.
     *
     * @param offset zero-based offset.
     * @param limit  the size of the elements to be returned.
     */
    constructor(offset: Int, limit: Int) : this(offset.toLong(), limit, Sort(Sort.Direction.ASC, "id")) {}

    override fun getPageNumber(): Int {
        return (offset / limit).toInt()
    }

    override fun getPageSize(): Int {
        return limit
    }

    override fun getOffset(): Long {
        return offset
    }

    override fun getSort(): Sort {
        return sort
    }

    override fun next(): Pageable {
        return OffsetBasedPageRequest(getOffset() + pageSize, pageSize, getSort())
    }

    fun previous(): OffsetBasedPageRequest {
        return if (hasPrevious()) OffsetBasedPageRequest(getOffset() - pageSize, pageSize, getSort()) else this
    }

    override fun previousOrFirst(): Pageable {
        return if (hasPrevious()) previous() else first()
    }

    override fun first(): Pageable {
        return OffsetBasedPageRequest(0, pageSize, getSort())
    }

    override fun hasPrevious(): Boolean {
        return offset > limit
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as OffsetBasedPageRequest
        return limit == that.limit && offset == that.offset &&
                sort == that.sort
    }

    override fun hashCode(): Int {
        return Objects.hash(limit, offset, sort)
    }

    override fun toString(): String {
        return "OffsetBasedPageRequest{" +
                "limit=" + limit +
                ", offset=" + offset +
                ", sort=" + sort +
                '}'
    }

    companion object {
        private const val serialVersionUID = 778067427248367015L
    }

    /**
     * Creates a new [OffsetBasedPageRequest] with sort parameters applied.
     *
     * @param offset zero-based offset.
     * @param limit  the size of the elements to be returned.
     * @param sort   can be null.
     */
    init {
        require(offset >= 0) { "Offset index must not be less than zero!" }
        require(limit >= 1) { "Limit must not be less than one!" }
        this.limit = limit
        this.offset = offset
        this.sort = sort
    }
}
