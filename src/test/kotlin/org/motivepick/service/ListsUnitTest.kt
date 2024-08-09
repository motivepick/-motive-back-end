package org.motivepick.service

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.motivepick.service.Lists.insertBefore

class ListsUnitTest {

    @Test
    fun shouldCorrectlyInsertElementToEmptyList() = assertThat(insertBefore(listOf(), 0, 1L), equalTo(listOf(1L)))

    @Test
    fun shouldCorrectlyInsertElementToTheBeginning() = assertThat(insertBefore(listOf(2L), 0, 1L), equalTo(listOf(1L, 2L)))

    @Test
    fun shouldCorrectlyInsertElementToTheMiddle() = assertThat(insertBefore(listOf(1L, 3L), 1, 2L), equalTo(listOf(1L, 2L, 3L)))

    @Test
    fun shouldCorrectlyInsertElementToTheEnd() = assertThat(insertBefore(listOf(1L), 1, 2L), equalTo(listOf(1L, 2L)))
}
