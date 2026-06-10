package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Material
import org.bukkit.inventory.meta.Damageable
import kotlin.math.min


object EffectRepairItem : Effect<NoCompileData>("repair_item") {
    override val description = "Repairs the triggering item by a specified amount of durability."
    override val categories = setOf("inventory")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "damage",
            "You must specify the amount of damage to repair!",
            description = "The amount of durability to restore. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false

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
