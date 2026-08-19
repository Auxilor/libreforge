package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorRoundValue : Mutator<NoCompileData>("round_value") {
    override val description = "Rounds the value to a whole number."

    override val categories = setOf("value", "meta")

    override val arguments = arguments {
        optional(
            "mode",
            description = "How to round the value.",
            type = ArgType.STRING,
            default = "nearest",
            choices = listOf("nearest", "up", "down"),
            example = "down"
        )
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val mode = if (config.has("mode")) config.getString("mode") else "nearest"

        return data.copy(
            value = roundValue(data.value, mode)
        )
    }
}
