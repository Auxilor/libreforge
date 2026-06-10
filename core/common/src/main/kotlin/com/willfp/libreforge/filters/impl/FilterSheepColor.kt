package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Sheep

object FilterSheepColor : Filter<NoCompileData, Collection<String>>("sheep_color") {
    override val description = "Matches when the victim sheep's wool color matches one of the given colors."
    override val categories = setOf("entity")
    override val valueType = ArgType.STRING_LIST

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val entity = data.victim as? Sheep ?: return false
        val sheepColor = entity.color?.name

        return value.any { it.equals(sheepColor, ignoreCase = true) }
    }
}
