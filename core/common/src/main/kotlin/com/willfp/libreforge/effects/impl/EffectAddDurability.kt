package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.inventory.meta.Damageable

object EffectAddDurability : Effect<NoCompileData>("add_durability") {
    override val description = "Increases the maximum durability of the triggering item."
    override val categories = setOf("inventory")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "durability",
            "You must specify the durability to add!",
            description = "The amount of maximum durability to add. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "50 + %level% * 10"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false
        val durability = config.getIntFromExpression("durability", data)
        val meta = item.itemMeta as? Damageable ?: return false

        val baseMaxDamage = if (meta.hasMaxDamage()) meta.maxDamage else item.type.maxDurability.toInt()
        if (baseMaxDamage <= 0) return false

        val newMaxDamage = baseMaxDamage + durability
        if (newMaxDamage <= 0) {
            item.amount = 0
            return true
        }

        meta.setMaxDamage(newMaxDamage)
        item.itemMeta = meta

        return true
    }
}
