package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Entity

object ConditionIsFalling : Condition<NoCompileData>("is_falling") {
    override val description = "Passes when the entity has a significant downward velocity, indicating it is falling."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Detected via a downward Y-velocity threshold rather than a falling flag — may briefly trigger at the apex of a jump."
    )

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val entity = dispatcher.get<Entity>() ?: return false

        return entity.velocity.y < -0.1
    }
}
