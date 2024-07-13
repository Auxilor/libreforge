package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.hasCondition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent

object ConditionOnFire : Condition<NoCompileData>("on_fire") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val entity = dispatcher.get<Entity>() ?: return false

        return entity.fireTicks > 0
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: EntityDamageEvent) {
        if (event.cause != EntityDamageEvent.DamageCause.FIRE
            && event.cause != EntityDamageEvent.DamageCause.FIRE_TICK
            && event.cause != EntityDamageEvent.DamageCause.HOT_FLOOR
            && event.cause != EntityDamageEvent.DamageCause.LAVA
        ) {
            return
        }

        val entity = event.entity.toDispatcher()

        if (!entity.hasCondition(this)) return

        entity.updateEffects()
    }
}
