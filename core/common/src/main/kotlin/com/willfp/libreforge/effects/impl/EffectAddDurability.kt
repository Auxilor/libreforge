package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.levels.LevelTypes
import com.willfp.libreforge.levels.levels
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.inventory.meta.Damageable

object EffectAddDurability : Effect<NoCompileData>("add_durability") {
    override val isPermanent = false

    override val arguments = arguments {
        require("durability", "You must specify the durability to add!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false
        val durability = config.getIntFromExpression("durability", data)
        val meta = item.itemMeta as? Damageable ?: return false

        meta.setMaxDamage(meta.maxDamage + durability)

        return true
    }
}
