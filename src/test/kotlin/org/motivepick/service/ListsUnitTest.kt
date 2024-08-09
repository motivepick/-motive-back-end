package org.motivepick.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.motivepick.service.Lists.insertBefore

class ListsUnitTest {

    @Test
    fun shouldCorrectlyInsertElementToEmptyList() {
        assertEquals(listOf(1L), insertBefore(listOf(), 0, 1L))
    }

    @Test
    fun shouldCorrectlyInsertElementToTheBeginning() {
        assertEquals(listOf(1L, 2L), insertBefore(listOf(2L), 0, 1L))
    }

    @Test
    fun shouldCorrectlyInsertElementToTheMiddle() {
        assertEquals(listOf(1L, 2L, 3L), insertBefore(listOf(1L, 3L), 1, 2L))
    }

    @Test
    fun shouldCorrectlyInsertElementToTheEnd() {
        assertEquals(listOf(1L, 2L), insertBefore(listOf(1L), 1, 2L))
    }
}
