package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent

object ConditionBelowHealth : Condition<NoCompileData>("below_health") {
    override val description = "Passes when the entity's health is at or below the given amount."
    override val categories = setOf("combat")

    override val arguments = arguments {
        require(
            "health",
            "You must specify the health!",
            description = "The maximum health.",
            type = ArgType.EXPRESSION,
            example = "6 + %level% * 0.5"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val livingEntity = dispatcher.get<LivingEntity>() ?: return false

        return livingEntity.health <= config.getDoubleFromExpression("health", livingEntity as? Player)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: EntityRegainHealthEvent) {
        event.entity.toDispatcher().updateEffects()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: EntityDamageEvent) {
        event.entity.toDispatcher().updateEffects()
    }
}
