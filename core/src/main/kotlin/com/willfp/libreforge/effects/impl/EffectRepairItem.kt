package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import kotlin.math.min


object EffectRepairItem : Effect<NoCompileData>("repair_item") {
    override val isPermanent = false

    override val arguments = arguments {
        require("damage", "You must specify the amount of damage to repair!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.holder.provider as? ItemStack ?: data.item ?: return false

        val damage = config.getIntFromExpression("damage", data)

        val meta = item.itemMeta ?: return false

        if (meta.isUnbreakable || meta !is Damageable) {
            return false
        }

        // Edge cases
        if (item.type == Material.CARVED_PUMPKIN || item.type == Material.PLAYER_HEAD) {
            return false
        }

        meta.damage -= min(damage, meta.damage)

        item.itemMeta = meta

        return true
    }
}
