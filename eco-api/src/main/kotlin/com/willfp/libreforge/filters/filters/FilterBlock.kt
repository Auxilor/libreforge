package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.block.Block
import java.util.function.Predicate

class FilterBlock(
    filterConfig: JSONConfig
) : Filter() {
    private val typeFilter = Predicate<Block> { block ->
        val materialNames = filterConfig.getStringsOrNull("blocks", false)
            ?.map { it.uppercase() } ?: emptyList()

        return@Predicate materialNames.contains(block.type.name)
    }

    override fun matches(data: TriggerData): Boolean {
        val block = data.block ?: return true

        return typeFilter.test(block)
    }

    override fun validateConfig(config: JSONConfig): List<com.willfp.libreforge.ConfigViolation> {
        return emptyList()
    }
}
