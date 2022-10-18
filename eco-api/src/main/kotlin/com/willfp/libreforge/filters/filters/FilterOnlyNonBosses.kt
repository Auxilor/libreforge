package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Boss
import org.bukkit.entity.ElderGuardian
import org.bukkit.persistence.PersistentDataType

object FilterOnlyNonBosses : Filter() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val entity = data.victim ?: return true

        return config.withInverse("only_non_bosses", Config::getBool) {
            (entity is Boss || entity is ElderGuardian || entity.persistentDataContainer
                .has(NamespacedKeyUtils.create("ecobosses", "boss"), PersistentDataType.STRING)) != it
        }
    }
}

