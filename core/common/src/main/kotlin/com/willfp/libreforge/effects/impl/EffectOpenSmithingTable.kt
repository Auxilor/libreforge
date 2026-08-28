package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectOpenSmithingTable : Effect<NoCompileData>("open_smithing_table") {
    override val description = "Opens a virtual smithing table GUI for the player."
    override val categories = setOf("player", "inventory")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        @Suppress("DEPRECATION")
        player.openSmithingTable(null, true)

        return true
    }
}
