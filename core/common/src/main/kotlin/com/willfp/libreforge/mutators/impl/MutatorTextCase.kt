package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorTextCase : Mutator<NoCompileData>("text_case") {
    override val description = "Changes the capitalisation of the text."

    override val categories = setOf("chat")

    override val arguments = arguments {
        require("case", "You must specify a case! (upper, lower, or title)", Config::getString) {
            it in listOf("upper", "lower", "title")
        }
        describe(
            "case",
            description = "The capitalisation to apply to the text.",
            type = ArgType.STRING,
            choices = listOf("upper", "lower", "title")
        )
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val text = data.text ?: return data

        return data.copy(
            text = when (config.getString("case").lowercase()) {
                "upper" -> text.uppercase()
                "lower" -> text.lowercase()
                else -> text.split(" ").joinToString(" ") { word ->
                    word.replaceFirstChar { it.uppercase() }
                }
            }
        )
    }
}
