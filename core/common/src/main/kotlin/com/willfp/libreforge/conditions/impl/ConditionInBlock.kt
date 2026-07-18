package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.LivingEntity

object ConditionInBlock : Condition<NoCompileData>("in_block") {
    override val description = "Passes when the entity's head or feet are inside the specified block type."
    override val categories = setOf("world")

    override val arguments = arguments {
        require(
            "block",
            "You must specify the block!",
            description = "The block material name to check (e.g. WATER).",
            type = ArgType.BLOCK
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val entity = dispatcher.get<LivingEntity>() ?: return false

        val world = entity.world
        val head = world.getBlockAt(entity.eyeLocation).type
        val feet = world.getBlockAt(entity.eyeLocation.clone().subtract(0.0, 1.0, 0.0)).type
        val block = config.getString("block")
        return head.name.equals(block, ignoreCase = true) || feet.name.equals(block, ignoreCase = true)
    }
}
