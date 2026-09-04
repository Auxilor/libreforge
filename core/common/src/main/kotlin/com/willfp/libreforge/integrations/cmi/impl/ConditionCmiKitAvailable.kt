package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.CMI
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionCmiKitAvailable : Condition<NoCompileData>("cmi_kit_available") {
    override val description = "Passes when the player is able to claim a specified kit."
    override val categories = setOf("player")
    override val additionalInfo = listOf(
        "Requires the CMI plugin.",
        "Does not pass if no kit with the given name exists."
    )

    override val arguments = arguments {
        require(
            "kit",
            "You must specify the kit name!",
            description = "The name of the CMI kit to check.",
            type = ArgType.STRING
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val user = CMI.getInstance()?.playerManager?.getUser(player) ?: return false
        val kit = CMI.getInstance()?.kitsManager?.getKit(config.getString("kit")) ?: return false

        return user.canUseKit(kit)
    }
}
