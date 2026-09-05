package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.floodgate.isBedrockPlayer
import com.willfp.libreforge.triggers.TriggerData

object FilterVictimIsBedrockPlayer : Filter<NoCompileData, Boolean>("victim_is_bedrock_player") {
    override val description = "Matches when the victim is (or is not) a player on Bedrock edition."

    override val categories = setOf("player", "victim")

    override val valueType = ArgType.BOOLEAN

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Passes automatically when no victim is present in the trigger data.",
        "Matches false for any victim that is not a player at all, such as a mob."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return true

        return value == isBedrockPlayer(victim)
    }
}
