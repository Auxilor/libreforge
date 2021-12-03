package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Boss
import org.bukkit.entity.ElderGuardian
import org.bukkit.entity.LivingEntity
import org.bukkit.persistence.PersistentDataType

class FilterOnlyBosses: FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val entity = data.victim ?: return true

        val onlyBosses = config.getBoolOrNull("onlyBosses") ?: false

        if (!onlyBosses) {
            return true
        }

        return entity is Boss || entity is ElderGuardian || entity.persistentDataContainer
            .has(NamespacedKeyUtils.create("ecobosses", "boss"), PersistentDataType.STRING)
    }
}
