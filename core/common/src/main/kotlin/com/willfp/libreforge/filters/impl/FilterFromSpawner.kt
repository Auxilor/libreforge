package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.namespacedKeyOf
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.persistence.PersistentDataType

object FilterFromSpawner : Filter<NoCompileData, Boolean>("from_spawner") {
    override val description = "Matches when the victim was (or was not) spawned by a mob spawner."
    override val categories = setOf("entity")
    override val valueType = ArgType.BOOLEAN
    override val additionalInfo = listOf("Passes automatically when no victim is present in the trigger data.")

    private val fromSpawnerKey = namespacedKeyOf("ecomobs", "from_spawner")

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return true

        val isFromSpawner = victim.persistentDataContainer.has(fromSpawnerKey, PersistentDataType.BYTE)
            || (Prerequisite.HAS_PAPER.isMet && victim.fromMobSpawner())

        return value == isFromSpawner
    }
}
