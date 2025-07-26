package com.willfp.libreforge.integrations.edprisoncore.impl

import com.edwardbelt.edprison.modules.pickaxe.PickaxeUtils
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectUpdateEdPrisonPickaxe : Effect<NoCompileData>("update_edprison_pickaxe") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        PickaxeUtils.updatePickaxe(player)
        return true
    }
}