package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getProvider
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.roundToInt

object EffectItemDurabilityMultiplier : Effect<NoCompileData>("item_durability_multiplier") {
    override val description = "Multiplies the durability loss of the held item, effectively making it more or less durable."
    override val categories = setOf("inventory")

    override val supportsDelay = false

    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the durability multiplier!",
            description = "The durability multiplier to apply (e.g. 2 means the item lasts twice as long). Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? PlayerItemDamageEvent ?: return false
        val item = data.holder.getProvider<ItemStack>() ?: return false

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
