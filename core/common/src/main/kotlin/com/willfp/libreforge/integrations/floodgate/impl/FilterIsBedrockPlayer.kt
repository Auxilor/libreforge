package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.floodgate.isBedrockPlayer
import com.willfp.libreforge.triggers.TriggerData

object FilterIsBedrockPlayer : Filter<NoCompileData, Boolean>("is_bedrock_player") {
    override val description = "Matches when the player is (or is not) playing on Bedrock edition."

    override val categories = setOf("player")

    override val valueType = ArgType.BOOLEAN

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Passes automatically when no player is present in the trigger data.",
        "Matches linked Bedrock players too, even though they are online under their Java account's UUID."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val player = data.player ?: return true

        return value == isBedrockPlayer(player)
    }
}
