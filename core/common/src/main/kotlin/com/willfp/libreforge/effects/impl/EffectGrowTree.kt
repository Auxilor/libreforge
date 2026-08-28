package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.TreeType

object EffectGrowTree : Effect<NoCompileData>("grow_tree") {
    override val description = "Grows a tree at the trigger location."
    override val categories = setOf("world")

    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require(
            "type",
            "You must specify the tree type!",
            description = "The type of tree to grow.",
            type = ArgType.STRING,
            enumClass = TreeType::class,
            example = "BIG_TREE"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val location = data.location ?: return false
        val world = location.world ?: return false

        val type = enumValueOfOrNull<TreeType>(
            config.getFormattedString("type", data).uppercase()
        ) ?: return false

        return world.generateTree(location, type)
    }
}
