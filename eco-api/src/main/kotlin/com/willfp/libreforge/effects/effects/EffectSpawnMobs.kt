package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.EntityType
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
        val world = location.world ?: return
        val victim = data.victim

        if (victim?.getMetadata("eco-target")?.isEmpty() == true) {
            return
        }

        val amount = config.getInt("amount")
        val ticksToLive = config.getInt("ticks_to_live")
        val health = config.getDouble("health")
        val range = config.getDouble("range")

        val entityType = EntityType.valueOf(config.getString("entity").uppercase())

        for (i in 1..amount) {
            val locationToSpawn = location.clone().add(
                NumberUtils.randFloat(-range, range),
                NumberUtils.randFloat(0.0, range),
                NumberUtils.randFloat(-range, range)
            )
            val mob = world.spawnEntity(locationToSpawn, entityType) as Mob

            if (victim != null) {
                mob.target = victim
                mob.setMetadata("eco-target", plugin.metadataValueFactory.create(victim))
            }

            mob.health = health

            this.plugin.scheduler.runLater({ mob.remove() }, ticksToLive.toLong())
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getIntOrNull("amount")
            ?: violations.add(
                ConfigViolation(
                    "amount",
                    "You must specify the amount of mobs to spawn!"
                )
            )

        config.getIntOrNull("ticks_to_live")
            ?: violations.add(
                ConfigViolation(
                    "ticks_to_live",
                    "You must specify the amount of ticks the mobs should live!"
                )
            )

        config.getDoubleOrNull("health")
            ?: violations.add(
                ConfigViolation(
                    "health",
                    "You must specify the mob health!"
                )
            )

        config.getDoubleOrNull("range")
            ?: violations.add(
                ConfigViolation(
                    "range",
                    "You must specify the range for mobs to spawn!"
                )
            )

        config.getStringOrNull("entity")
            ?: violations.add(
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