package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityTargetEvent


class EffectSpawnMobs : Effect(
    "spawn_mobs",
    supportsFilters = false,
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.LOCATION
    )
), Listener {
    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return
        location.world ?: return
        val victim = if (config.getBool("self_as_victim")) data.player else data.victim

        if (victim != null) {
            if (victim.getMetadata("eco-target").isNotEmpty()) {
                return
            }
        }

        val amount = config.getIntFromExpression("amount", data.player)
        val ticksToLive = config.getIntFromExpression("ticks_to_live", data.player)
        val health = config.getDoubleFromExpression("health", data.player)
        val range = config.getDoubleFromExpression("range", data.player)

        val entityType = Entities.lookup(config.getString("entity"))

        for (i in 1..amount) {
            val locationToSpawn = location.clone().add(
                NumberUtils.randFloat(-range, range),
                NumberUtils.randFloat(0.0, range),
                NumberUtils.randFloat(-range, range)
            )
            val mob = entityType.spawn(locationToSpawn) as Mob
            val healthAttr = mob.getAttribute(Attribute.GENERIC_MAX_HEALTH) ?: continue
            healthAttr.baseValue = health

            if (victim != null) {
                mob.target = victim
                mob.setMetadata("eco-target", plugin.metadataValueFactory.create(victim))
            }

            mob.health = health

            this.plugin.scheduler.runLater(ticksToLive.toLong()) { mob.remove() }
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("amount")) violations.add(
            ConfigViolation(
                "amount",
                "You must specify the amount of mobs to spawn!"
            )
        )

        if (!config.has("ticks_to_live")) violations.add(
            ConfigViolation(
                "ticks_to_live",
                "You must specify the amount of ticks the mobs should live!"
            )
        )

        if (!config.has("range")) violations.add(
            ConfigViolation(
                "range",
                "You must specify the range for mobs to spawn!"
            )
        )

        if (!config.has("entity")) violations.add(
            ConfigViolation(
                "entity",
                "You must specify the mob to spawn!"
            )
        )

        return violations
    }

    @EventHandler
    fun onSwitchTarget(event: EntityTargetEvent) {
        if (event.entity.getMetadata("eco-target").isEmpty()) {
            return
        }
        val target = event.entity.getMetadata("eco-target")[0].value() as LivingEntity?
        event.target = target
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onDropItem(event: EntityDeathEvent) {
        if (event.entity.getMetadata("eco-target").isEmpty()) {
            return
        }
        event.drops.clear()
        event.droppedExp = 0
    }
}