package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectWearHeldItem : Effect<NoCompileData>("wear_held_item") {
    override val description = "Swaps the item in the player's main hand with whatever they are wearing on their head."
    override val categories = setOf("player", "inventory")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val inventory = player.inventory

        val held = inventory.itemInMainHand
        val helmet = inventory.helmet

        if (held.type.isAir && helmet == null) {
            return false
        }

        inventory.helmet = if (held.type.isAir) null else held
        inventory.setItemInMainHand(helmet)

        return true
    }
}
