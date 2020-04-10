package org.motivepick.service

object Lists {

    fun insertWithShift(list: List<Long?>, index: Int, element: Long?): List<Long?> =
            list.subList(0, index) + element + list.subList(index, list.size)
}
