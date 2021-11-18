@file:JvmName("LibReforgeUtils")

package com.willfp.libreforge.api

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.eco.util.ListUtils
import com.willfp.libreforge.api.conditions.Conditions
import com.willfp.libreforge.api.provider.Holder
import com.willfp.libreforge.api.provider.HolderProvider
import com.willfp.libreforge.internal.WatcherTriggers
import com.willfp.libreforge.internal.integrations.aureliumskills.AureliumSkillsIntegration
import com.willfp.libreforge.internal.integrations.ecoskills.EcoSkillsIntegration
import org.apache.commons.lang.StringUtils
import org.bukkit.entity.Player
import java.util.UUID
import java.util.WeakHashMap

private val holderProviders = mutableSetOf<HolderProvider>()
private val previousStates: MutableMap<UUID, Iterable<Holder>> = WeakHashMap()
private val holderCache = mutableMapOf<UUID, Iterable<Holder>>()

object LibReforge {
    @JvmStatic
    lateinit var plugin: EcoPlugin

    private val defaultPackage = StringUtils.join(
        arrayOf("com", "willfp", "libreforge", "api"),
        "."
    )

    @JvmStatic
    fun init(plugin: EcoPlugin) {
        this.plugin = plugin

        if (this.javaClass.packageName == defaultPackage) {
            throw IllegalStateException("You must shade and relocate libreforge into your jar!")
        }
    }

    @JvmStatic
    fun enable(plugin: EcoPlugin) {
        plugin.eventManager.registerListener(WatcherTriggers(plugin))
        for (condition in Conditions.values()) {
            plugin.eventManager.registerListener(condition)
        }
    }

    @JvmStatic
    fun getIntegrationLoaders(): List<IntegrationLoader> {
        return listOf(
            IntegrationLoader("EcoSkills", EcoSkillsIntegration::load),
            IntegrationLoader("AureliumSkills", AureliumSkillsIntegration::load),
        )
    }

    @JvmStatic
    fun registerHolderProvider(provider: HolderProvider) {
        holderProviders.add(provider)
    }
}

private fun Player.clearEffectCache() {
    holderCache.remove(this.uniqueId)
}

fun Player.getHolders(): Iterable<Holder> {
    if (holderCache.containsKey(this.uniqueId)) {
        return holderCache[this.uniqueId]?.toList() ?: emptyList()
    }

    val holders = mutableListOf<Holder>()
    for (provider in holderProviders) {
        holders.addAll(provider.providerHolders(this))
    }

    holderCache[this.uniqueId] = holders
    LibReforge.plugin.scheduler.runLater({
        holderCache.remove(this.uniqueId)
    }, 40)

    return holders
}

fun Player.updateEffects() {
    val before = mutableListOf<Holder>()
    if (previousStates.containsKey(this.uniqueId)) {
        before.addAll(previousStates[this.uniqueId] ?: emptyList())
    }
    this.clearEffectCache()

    LibReforge.plugin.scheduler.run {
        val after = this.getHolders()
        previousStates[this.uniqueId] = after

        val beforeFreq = ListUtils.listToFrequencyMap(before)
        val afterFreq = ListUtils.listToFrequencyMap(after.toList())

        val added = mutableListOf<Holder>()
        val removed = mutableListOf<Holder>()

        for ((holder, freq) in afterFreq) {
            var amount = freq
            amount -= beforeFreq[holder] ?: 0
            if (amount < 1) {
                continue
            }

            for (i in 0 until amount) {
                added.add(holder)
            }
        }

        for ((holder, freq) in beforeFreq) {
            var amount = freq

            amount -= afterFreq[holder] ?: 0
            if (amount < 1) {
                continue
            }
            for (i in 0 until amount) {
                removed.add(holder)
            }
        }

        for (holder in added) {
            var areConditionsMet = true
            for ((condition, config) in holder.conditions) {
                if (!condition.isConditionMet(this, config)) {
                    areConditionsMet = false
                    break
                }
            }

            if (areConditionsMet) {
                for ((effect, config) in holder.effects) {
                    effect.enableForPlayer(this, config)
                }
            }
        }
        for (holder in removed) {
            for ((effect, _) in holder.effects) {
                effect.disableForPlayer(this)
            }
        }

        for (holder in after) {
            var areConditionsMet = true
            for ((condition, config) in holder.conditions) {
                if (!condition.isConditionMet(this, config)) {
                    areConditionsMet = false
                    break
                }
            }
            if (!areConditionsMet) {
                for ((effect, _) in holder.effects) {
                    effect.disableForPlayer(this)
                }
            }
        }
    }
}
