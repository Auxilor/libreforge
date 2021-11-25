package com.willfp.libreforge.filters

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.block.Block
import org.bukkit.entity.Boss
import org.bukkit.entity.ElderGuardian
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.persistence.PersistentDataType
import java.util.function.Predicate

open class ConfiguredFilter(
    private val config: JSONConfig
): Filter {
    private val entityTypeFilter = Predicate<LivingEntity> { entity ->
        val entityNames = config.getStringsOrNull("entities", false)
            ?.map { it.lowercase() } ?: emptyList()

        return@Predicate entityNames.contains(entity.type.name.lowercase())
    }

    private val bossFilter = Predicate<LivingEntity> { entity ->
        val onlyBosses = config.getBoolOrNull("onlyBosses") ?: false

        if (!onlyBosses) {
            return@Predicate true
        }

        return@Predicate entity is Boss || entity is ElderGuardian || entity.persistentDataContainer
            .has(NamespacedKeyUtils.create("ecobosses", "boss"), PersistentDataType.STRING)
    }

    private val materialTypeFilter = Predicate<Block> { block ->
        val materialNames = config.getStringsOrNull("blocks", false)
            ?.map { it.uppercase() } ?: emptyList()

        return@Predicate materialNames.contains(block.type.name)
    }

    private val damageCauseFilter = Predicate<EntityDamageEvent.DamageCause> { cause ->
        val causeNames = config.getStringsOrNull("damageCause", false)
            ?.map { it.uppercase() } ?: emptyList()

        return@Predicate causeNames.contains(cause.name)
    }

    override fun matches(data: TriggerData): Boolean {
        val testResults = mutableListOf<Boolean>()

        if (data.victim != null) {
            testResults.addAll(
                entityTypeFilter.test(data.victim),
                bossFilter.test(data.victim)
            )
        }

        if (data.block != null) {
            testResults.addAll(
                materialTypeFilter.test(data.block)
            )
        }

        if (data.damageCause != null) {
            testResults.addAll(
                damageCauseFilter.test(data.damageCause)
            )
        }

        return testResults.isEmpty() || testResults.stream().allMatch { it }
    }
}

private fun <T> MutableCollection<T>.addAll(vararg elements: T) {
    this.addAll(elements.toList())
}
