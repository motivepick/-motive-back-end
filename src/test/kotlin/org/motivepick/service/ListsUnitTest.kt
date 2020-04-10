package org.motivepick.service

import org.junit.Assert.assertEquals
import org.junit.Test
import org.motivepick.service.Lists.insertWithShift

class ListsUnitTest {

    @Test
    fun shouldCorrectlyInsertElementToEmptyList() {
        assertEquals(listOf(1L), insertWithShift(listOf(), 0, 1L))
    }

    @Test
    fun shouldCorrectlyInsertElementToTheBeginning() {
        assertEquals(listOf(1L, 2L), insertWithShift(listOf(2L), 0, 1L))
    }

    @Test
    fun shouldCorrectlyInsertElementToTheMiddle() {
        assertEquals(listOf(1L, 2L, 3L), insertWithShift(listOf(1L, 3L), 1, 2L))
    }

    @Test
    fun shouldCorrectlyInsertElementToTheEnd() {
        assertEquals(listOf(1L, 2L), insertWithShift(listOf(1L), 1, 2L))
    }
}
