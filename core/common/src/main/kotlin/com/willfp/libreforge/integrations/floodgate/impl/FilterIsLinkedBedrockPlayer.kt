package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.floodgate.bedrockPlayerOf
import com.willfp.libreforge.triggers.TriggerData

object FilterIsLinkedBedrockPlayer : Filter<NoCompileData, Boolean>("is_linked_bedrock_player") {
    override val description = "Matches when the player is (or is not) a Bedrock player with a linked Java account."

    override val categories = setOf("player")

    override val valueType = ArgType.BOOLEAN

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Passes automatically when no player is present in the trigger data.",
        "Matches false for Java players, who cannot be linked Bedrock players by definition.",
        "A linked player is online under their Java account's UUID and name, not a Floodgate one."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val player = data.player ?: return true

        return value == (bedrockPlayerOf(player)?.isLinked == true)
    }
}
