package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Mob

object EffectRaiseUndead : Effect<TestableEntity>("raise_undead") {
    override val parameters = setOf(
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("entity", "You must specify the entity type to raise!")
        require("ticks_to_live", "You must specify the lifespan in ticks!")
        require("health", "You must specify the health!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: TestableEntity): Boolean {
        val location = data.location ?: return false
        location.world ?: return false

        val health = config.getDoubleFromExpression("health", data)
        val ticksToLive = config.getIntFromExpression("ticks_to_live", data)
        val player = data.player

        val mob = compileData.spawn(location) as? Mob ?: return false

        mob.getAttribute(Attribute.MAX_HEALTH)?.baseValue = health
        mob.health = health

        if (player != null) {
            mob.setMetadata("raise-undead-avoid", plugin.createMetadataValue(player.uniqueId))
        }

        plugin.scheduler.runTaskLater(mob, ticksToLive.toLong()) { mob.remove() }

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): TestableEntity {
        return Entities.lookup(config.getString("entity"))
    }
}
