package com.willfp.libreforge

data class ConfigViolation(val param: String, val message: String)

class ViolationContext {
    private val parents: List<String>

    constructor() {
        parents = listOf()
    }

    internal constructor(parents: List<String>) {
        this.parents = parents
    }

    fun with(context: String) = ViolationContext(parents + context)

    override fun toString(): String {
        return parents.joinToString(" -> ")
    }
}
