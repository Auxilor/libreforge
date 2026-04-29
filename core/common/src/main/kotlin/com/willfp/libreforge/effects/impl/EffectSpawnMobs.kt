package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityTargetEvent
import java.util.UUID


object EffectSpawnMobs : Effect<TestableEntity>("spawn_mobs") {
    override val parameters: Set<TriggerParameter> = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("amount", "You must specify the amount of mobs to spawn!")
        require("ticks_to_live", "You must specify the mob lifespan!")
        require("range", "You must specify the range to spawn in!")
        require("entity", "You must specify the mob to spawn!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: TestableEntity): Boolean {
        val location = data.location ?: return false
        location.world ?: return false
        val victim = data.victim
        val player = data.player

        if (victim != null) {
            if (victim.getMetadata("spawn-mobs-target").isNotEmpty()) {
                return false
            }
        }

        val amount = config.getIntFromExpression("amount", data)
        val ticksToLive = config.getIntFromExpression("ticks_to_live", data)
        val health = config.getDoubleFromExpression("health", data)
        val range = config.getDoubleFromExpression("range", data)

        val entityType = Entities.lookup(config.getString("entity"))

        for (i in 1..amount) {
            val locationToSpawn = location.clone().add(
                NumberUtils.randFloat(-range, range),
                NumberUtils.randFloat(0.0, range),
                NumberUtils.randFloat(-range, range)
            )
            val mob = entityType.spawn(locationToSpawn) as Mob
            val healthAttr = mob.getAttribute(Attribute.MAX_HEALTH) ?: continue
            healthAttr.baseValue = health

            if (victim != null) {
                mob.target = victim
                mob.setMetadata("spawn-mobs-target", plugin.createMetadataValue(victim))
            }

            if (player != null) {
                mob.setMetadata("spawn-mobs-avoid", plugin.createMetadataValue(player.uniqueId))
            }

            mob.health = health

            plugin.scheduler.runLater(ticksToLive.toLong()) { mob.remove() }
        }

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): TestableEntity {
        return Entities.lookup(config.getString("entity"))
    }

    @EventHandler
    fun onSwitchTarget(event: EntityTargetEvent) {
        if (event.entity.getMetadata("spawn-mobs-target").isNotEmpty()) {
            val target = event.entity.getMetadata("spawn-mobs-target")[0].value() as? LivingEntity ?: return
            event.target = target
        }

        if (event.entity.getMetadata("spawn-mobs-avoid").isNotEmpty()) {
            val uuid = event.entity.getMetadata("spawn-mobs-avoid")[0].value() as? UUID ?: return
            if (event.target?.uniqueId == uuid) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onDropItem(event: EntityDeathEvent) {
        if (event.entity.getMetadata("spawn-mobs-target").isEmpty()) {
            return
        }

        event.drops.clear()

        event.droppedExp = 0
    }
}
