package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSpawnEntity : Effect<TestableEntity>("spawn_entity") {
    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("entity", "You must specify the mob to spawn!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: TestableEntity): Boolean {
        val location = data.location ?: return false
        compileData.spawn(location)

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): TestableEntity {
        return Entities.lookup(config.getString("entity"))
    }
}
