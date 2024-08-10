package org.motivepick.service

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.motivepick.service.ListExtensions.add

class ListExtensionsUnitTest {

    @Test
    fun shouldAddElementToEmptyList() = assertThat(listOf<Long>().add(0, 1L), equalTo(listOf(1L)))

    @Test
    fun shouldAddElementToBeginning() = assertThat(listOf(2L).add(0, 1L), equalTo(listOf(1L, 2L)))

    @Test
    fun shouldAddElementToMiddle() = assertThat(listOf(1L, 3L).add(1, 2L), equalTo(listOf(1L, 2L, 3L)))

    @Test
    fun shouldAddElementToEnd() = assertThat(listOf(1L).add(1, 2L), equalTo(listOf(1L, 2L)))
}
