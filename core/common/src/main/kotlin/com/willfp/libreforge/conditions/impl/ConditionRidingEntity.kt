package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.hasCondition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.spigotmc.event.entity.EntityDismountEvent
import org.spigotmc.event.entity.EntityMountEvent

object ConditionRidingEntity : Condition<Collection<TestableEntity>>("riding_entity") {
    override val arguments = arguments {
        require("entities", "You must specify the list of allowed entities!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: Collection<TestableEntity>
    ): Boolean {
        val entity = dispatcher.get<Entity>() ?: return false

        return compileData.any { it.matches(entity.vehicle) }
    }

    override fun makeCompileData(config: Config, context: ViolationContext): Collection<TestableEntity> {
        return config.getStrings("entities").map {
            Entities.lookup(it)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: EntityDismountEvent) {
        val entity = event.entity.toDispatcher()

        if (!entity.hasCondition(this)) return

        entity.updateEffects()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: EntityMountEvent) {
        val entity = event.entity.toDispatcher()

        if (!entity.hasCondition(this)) return

        entity.updateEffects()
    }
}
