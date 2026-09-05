package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.floodgate.bedrockPlayerOf
import com.willfp.libreforge.integrations.floodgate.namesEnum
import com.willfp.libreforge.triggers.TriggerData

object FilterBedrockDeviceOs : Filter<NoCompileData, Collection<String>>("bedrock_device_os") {
    override val description = "Matches when the player is on Bedrock edition using one of the given devices."

    override val categories = setOf("player")

    override val valueType = ArgType.STRING_LIST

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Passes automatically when no player is present in the trigger data.",
        "Never matches Java players, as there is no device information for them.",
        "Both the constant name and the display name are accepted, so GOOGLE and Android are the same device, as are NX and Switch."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val player = data.player ?: return true

        val bedrock = bedrockPlayerOf(player) ?: return false

        return value.namesEnum(bedrock.deviceOs)
    }
}
