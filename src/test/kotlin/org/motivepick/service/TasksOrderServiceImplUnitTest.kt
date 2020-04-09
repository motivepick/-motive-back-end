package org.motivepick.service

import org.junit.Test

class TasksOrderServiceImplUnitTest {

    @Test
    fun test() {
        val list: List<Int> = listOf(1, 2, 3)
        println(insertWithShift(list, 1, 4))
    }

    fun insertWithShift(list: List<Int>, index: Int, element: Int): List<Int> {
        val result: MutableList<Int> = mutableListOf()
        for ((i, value) in list.iterator().withIndex()) {
            if (i == index) {
                result.add(element)
            }
            result.add(value)
        }
        return result.toList()
    }
}