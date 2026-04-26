package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectClearInventory : Effect<NoCompileData>("clear_inventory") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("type", "You must specify the inventory type (all/hotbar/main/armor)!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        when (config.getString("type").lowercase()) {
            "all" -> player.inventory.clear()
            "hotbar" -> (0..8).forEach { player.inventory.setItem(it, null) }
            "main" -> (9..35).forEach { player.inventory.setItem(it, null) }
            "armor" -> player.inventory.armorContents = arrayOfNulls(4)
            else -> return false
        }

        return true
    }
}
