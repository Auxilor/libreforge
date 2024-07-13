package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.hasCondition
import com.willfp.libreforge.updateEffects
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent

object ConditionBelowHealthPercent : Condition<NoCompileData>("below_health_percent") {
    override val arguments = arguments {
        require("percent", "You must specify the health percentage!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val livingEntity = dispatcher.get<LivingEntity>() ?: return false

        val maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: return false
        val health = livingEntity.health

        return health / maxHealth <= config.getDoubleFromExpression("percent", livingEntity as? Player) / 100
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: EntityRegainHealthEvent) {

        val entity = event.entity.toDispatcher()

        if (!entity.hasCondition(this)) return

        entity.updateEffects()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: EntityDamageEvent) {

        val entity = event.entity.toDispatcher()

        if (!entity.hasCondition(this)) return

        entity.updateEffects()
    }
}
