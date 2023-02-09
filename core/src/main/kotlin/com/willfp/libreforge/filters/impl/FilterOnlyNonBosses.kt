package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Boss
import org.bukkit.entity.ElderGuardian
import org.bukkit.persistence.PersistentDataType

object FilterOnlyNonBosses : Filter<NoCompileData, Boolean>("only_non_bosses") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val entity = data.victim ?: return true
        return (entity is Boss || entity is ElderGuardian || entity.persistentDataContainer
            .has(NamespacedKeyUtils.create("ecobosses", "boss"), PersistentDataType.STRING)) != value
    }
}
