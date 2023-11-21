package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.IdentifiedModifier
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.get
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityShootBowEvent
import java.util.UUID
import kotlin.math.min

object EffectRapidBows : Effect<NoCompileData>("rapid_bows") {
    override val arguments = arguments {
        require("percent_faster", "You must specify how many percent faster to make bow pulls!")
    }

    private val modifiers = listMap<UUID, IdentifiedModifier>()

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        modifiers[dispatcher.uuid] += IdentifiedModifier(identifiers.uuid) {
            config.getDoubleFromExpression("percent_faster", dispatcher.get())
        }
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        modifiers[dispatcher.uuid].removeIf { it.uuid == identifiers.uuid }
    }

    @EventHandler(
        priority = EventPriority.LOW,
        ignoreCancelled = true
    )
    fun handle(event: EntityShootBowEvent) {
        val entity = event.entity

        val totalPercentFaster = modifiers[entity.uniqueId]
            .sumOf { it.modifier }
            .coerceAtMost(100.0)

        val multiplier = 1 - totalPercentFaster / 100

        if (event.force < multiplier) {
            return
        }

        val force = min(1.0 / event.force, Double.MAX_VALUE)
        var velocity = event.projectile.velocity.multiply(force)

        if (velocity.length() > 3) {
            velocity = velocity.normalize().multiply(3)
        }

        event.projectile.velocity = velocity
    }
}
