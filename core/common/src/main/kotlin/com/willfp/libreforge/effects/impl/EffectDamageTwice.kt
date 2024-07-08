package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.entity.EntityDamageByEntityEvent

object EffectDamageTwice : Effect<NoCompileData>("damage_twice") {
    override val supportsDelay = false

    override val parameters = setOf(
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT
    )

    private const val META_KEY = "libreforge-damaged-twice"

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? EntityDamageByEntityEvent ?: return false
        val victim = data.victim ?: return false

        if (victim.hasMetadata(META_KEY)) {
            return false
        }

        plugin.scheduler.run {
            victim.setMetadata(META_KEY, plugin.createMetadataValue(true))
            victim.noDamageTicks = 0
            victim.damage(event.damage, event.damager)
        }

        return true
    }
}
