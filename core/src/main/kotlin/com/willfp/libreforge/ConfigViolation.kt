package com.willfp.libreforge

/**
 * An invalid configuration will flag up a violation.
 */
data class ConfigViolation(val param: String, val message: String)

/**
 * A context in which a violation occurred.
 */
class ViolationContext {
    private val parents: List<String>

    constructor() {
        parents = listOf()
    }

    constructor(context: String) {
        parents = listOf(context)
    }

    internal constructor(parents: List<String>) {
        this.parents = parents
    }

    /**
     * Copy the violation context with an extra added context.
     */
    fun with(context: String) = ViolationContext(parents + context)

    override fun toString(): String {
        return parents.joinToString(" -> ")
    }
}
