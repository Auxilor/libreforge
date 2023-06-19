package com.willfp.libreforge.mutators

import com.willfp.libreforge.triggers.TriggerParameter

data class TriggerParameterTransformer(
    val parameterIn: TriggerParameter,
    val parameterOut: TriggerParameter
) {
    fun transform(parameters: Set<TriggerParameter>): Set<TriggerParameter> {
        return parameters.fold(mutableSetOf()) { acc, parameter ->
            if (parameter == parameterIn) {
                acc.apply {
                    add(parameterIn)
                    add(parameterOut)
                }
            } else {
                acc.apply { add(parameter) }
            }
        }
    }
}

class TriggerParameterBuilder {
    private val set = mutableSetOf<TriggerParameterTransformer>()

    infix fun TriggerParameter.becomes(other: TriggerParameter) {
        set += TriggerParameterTransformer(this, other)
    }

    internal fun toSet(): Set<TriggerParameterTransformer> {
        return set
    }
}

fun parameterTransformers(block: TriggerParameterBuilder.() -> Unit): Set<TriggerParameterTransformer> =
    TriggerParameterBuilder().apply(block).toSet()
