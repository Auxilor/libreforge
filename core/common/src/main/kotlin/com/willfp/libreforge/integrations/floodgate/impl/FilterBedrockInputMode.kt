package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.floodgate.bedrockPlayerOf
import com.willfp.libreforge.integrations.floodgate.namesEnum
import com.willfp.libreforge.triggers.TriggerData

object FilterBedrockInputMode : Filter<NoCompileData, Collection<String>>("bedrock_input_mode") {
    override val description = "Matches when the player is on Bedrock edition using one of the given input methods."

    override val categories = setOf("player")

    override val valueType = ArgType.STRING_LIST

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Passes automatically when no player is present in the trigger data.",
        "Never matches Java players, as there is no input information for them.",
        "Accepted values are KEYBOARD_MOUSE, TOUCH, CONTROLLER, VR, and UNKNOWN."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val player = data.player ?: return true

        val bedrock = bedrockPlayerOf(player) ?: return false

        return value.namesEnum(bedrock.inputMode)
    }
}
