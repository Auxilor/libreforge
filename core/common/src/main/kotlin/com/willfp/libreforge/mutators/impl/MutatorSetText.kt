package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorSetText : Mutator<NoCompileData>("set_text") {
    override val description = "Sets the text to a fixed value, with placeholders."

    override val categories = setOf("chat", "meta")

    override val arguments = arguments {
        require(
            "text",
            "You must specify the text!",
            description = "The text to set, supporting placeholders.",
            type = ArgType.STRING,
            example = "%player_name% is level %level%"
        )
    }

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.TEXT)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            text = config.getFormattedString("text", data)
        )
    }
}
