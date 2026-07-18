package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.block.CreatureSpawner

object FilterSpawnerEntity : Filter<NoCompileData, Collection<String>>("spawner_entity") {
    override val description = "Matches when the block is a spawner and its spawn type matches one of the given entity types."
    override val categories = setOf("world", "entity")
    override val valueType = ArgType.STRING_LIST

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val block = data.block ?: return true
        val state = block.state as? CreatureSpawner ?: return false

        val spawnerEntityType = state.spawnedType?.name ?: "null"

        return value.containsIgnoreCase(spawnerEntityType)
    }
}
