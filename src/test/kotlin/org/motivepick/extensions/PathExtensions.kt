package org.motivepick.extensions

import java.nio.file.Path

object PathExtensions {

    internal fun Path.readTextFromResource(): String =
        object {}.javaClass.classLoader.getResource(this.toString())?.readText() ?: error("Resource not found: $this")
}
