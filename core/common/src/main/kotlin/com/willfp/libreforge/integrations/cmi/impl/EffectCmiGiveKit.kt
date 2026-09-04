package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.CMI
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getOrElse
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectCmiGiveKit : Effect<NoCompileData>("cmi_give_kit") {
    override val description = "Gives the player a CMI kit."
    override val categories = setOf("player")
    override val additionalInfo = listOf(
        "Requires the CMI plugin.",
        "Does nothing if no kit with the given name exists."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "kit",
            "You must specify the kit name!",
            description = "The name of the CMI kit to give.",
            type = ArgType.STRING
        )
        optional(
            "give_items",
            description = "Whether to give the kit's items as well as running its commands. Defaults to true.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val kitsManager = CMI.getInstance()?.kitsManager ?: return false
        val kit = kitsManager.getKit(config.getString("kit")) ?: return false

        kitsManager.giveKit(player, kit, config.getOrElse("give_items", true) { getBool(it) })

        return true
    }
}
