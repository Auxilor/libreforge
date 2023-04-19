package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.roundToInt

object EffectItemDurabilityMultiplier : Effect<NoCompileData>("item_durability_multiplier") {
    override val supportsDelay = false

    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override val arguments = arguments {
        require("multiplier", "You must specify the durability multiplier!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? PlayerItemDamageEvent ?: return false
        val item = data.holder.provider as? ItemStack ?: return false

        if (event.item != item) {
            return false
        }

        val multiplier = 1 / config.getDoubleFromExpression("multiplier", data)

        if (multiplier > 1.0) {
            event.isCancelled = NumberUtils.randFloat(0.0, 1.0) > multiplier
        } else {
            event.damage = (event.damage * multiplier).roundToInt()
        }

        return true
    }
}
