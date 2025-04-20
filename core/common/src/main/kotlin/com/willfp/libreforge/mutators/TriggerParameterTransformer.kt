package com.willfp.libreforge.mutators

import com.willfp.libreforge.triggers.TriggerParameter

data class TriggerParameterTransformer(
    val parameterIn: TriggerParameter?,
    val parameterOut: TriggerParameter
) {
    fun transform(parameters: Set<TriggerParameter>): Set<TriggerParameter> {
        return parameters.fold<TriggerParameter, MutableSet<TriggerParameter>>(mutableSetOf()) { acc, parameter ->
            if (parameter == parameterIn) {
                acc.apply {
                    add(parameterIn)
                    add(parameterOut)
                }
            } else {
                acc.apply { add(parameter) }
            }
        }.plus(parameterOut)
    }
}

class TriggerParameterBuilder {
    private val set = mutableSetOf<TriggerParameterTransformer>()

    infix fun TriggerParameter.becomes(other: TriggerParameter) {
        set += TriggerParameterTransformer(this, other)
    }

    fun adds(other: TriggerParameter) {
        set += TriggerParameterTransformer(null, other)
    }

    internal fun toSet(): Set<TriggerParameterTransformer> {
        return set
    }
}

fun parameterTransformers(block: TriggerParameterBuilder.() -> Unit): Set<TriggerParameterTransformer> =
    TriggerParameterBuilder().apply(block).toSet()
