package com.willfp.libreforge.internal.filter

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.libreforge.api.effects.ConfigViolation
import com.willfp.libreforge.api.filter.Filter
import org.bukkit.block.Block
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

    override fun matches(obj: Any): Boolean {
        if (obj !is LivingEntity) {
            return false
        }

        return typeFilter.test(obj) && bossFilter.test(obj)
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        return emptyList()
    }
}
