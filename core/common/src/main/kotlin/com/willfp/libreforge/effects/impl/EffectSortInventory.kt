package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSortInventory : Effect<NoCompileData>("sort_inventory") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("type", "You must specify the inventory type (all/hotbar/main)!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val slots = when (config.getString("type").lowercase()) {
            "all" -> 0..35
            "hotbar" -> 0..8
            "main" -> 9..35
            else -> return false
        }

        val sorted = slots.map { player.inventory.getItem(it) }
            .sortedWith(compareBy({ it?.type?.name ?: "￿" }, { -(it?.amount ?: 0) }))

        slots.zip(sorted).forEach { (slot, item) -> player.inventory.setItem(slot, item) }

        return true
    }
}
