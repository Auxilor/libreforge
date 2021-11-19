package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Boss
import org.bukkit.entity.ElderGuardian
import org.bukkit.entity.LivingEntity
import org.bukkit.persistence.PersistentDataType
import java.util.function.Predicate

class FilterLivingEntity(
    filterConfig: JSONConfig
) : Filter() {
    private val typeFilter = Predicate<LivingEntity> { entity ->
        val entityNames = filterConfig.getStringsOrNull("entities", false)
            ?.map { it.lowercase() } ?: emptyList()

        return@Predicate entityNames.contains(entity.type.name.lowercase())
    }

    private val bossFilter = Predicate<LivingEntity> { entity ->
        val onlyBosses = filterConfig.getBoolOrNull("onlyBosses") ?: false

        if (!onlyBosses) {
            return@Predicate true
        }

        return@Predicate entity is Boss || entity is ElderGuardian || entity.persistentDataContainer
            .has(NamespacedKeyUtils.create("ecobosses", "boss"), PersistentDataType.STRING)
    }

    override fun matches(data: TriggerData): Boolean {
        val victim = data.victim ?: return true

        return typeFilter.test(victim) && bossFilter.test(victim)
    }

    override fun validateConfig(config: JSONConfig): List<com.willfp.libreforge.ConfigViolation> {
        return emptyList()
    }
}
