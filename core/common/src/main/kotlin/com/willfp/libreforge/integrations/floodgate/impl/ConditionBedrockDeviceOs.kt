package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.integrations.floodgate.bedrockPlayerOf
import com.willfp.libreforge.integrations.floodgate.namesEnum
import org.bukkit.entity.Player
import org.geysermc.floodgate.util.DeviceOs

object ConditionBedrockDeviceOs : Condition<NoCompileData>("bedrock_device_os") {
    override val description = "Passes when the player is on Bedrock edition using one of the given devices."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Never passes for Java players, as there is no device information for them.",
        "Both the constant name and the display name are accepted, so GOOGLE and Android are the same device, as are NX and Switch."
    )

    override val arguments = arguments {
        require(
            listOf("devices", "device"),
            "You must specify the device(s)!",
            description = "The Bedrock devices to match.",
            type = ArgType.STRING_LIST,
            choices = DeviceOs.values().map { it.name },
            example = listOf("ANDROID", "IOS")
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        val bedrock = bedrockPlayerOf(player) ?: return false

        return config.getStrings("devices", "device").namesEnum(bedrock.deviceOs)
    }
}
