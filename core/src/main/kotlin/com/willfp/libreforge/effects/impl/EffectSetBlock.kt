package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material

object EffectSetBlock : Effect<Material>("set_block") {
    override val parameters = setOf(
        TriggerParameter.BLOCK
    )

    override val arguments = arguments {
        require("block", "You must specify the block!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: Material): Boolean {
        val block = data.block ?: data.location?.block ?: return false

        block.type = compileData

        return true
    }
}
