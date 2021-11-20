@file:JvmName("LibReforgeUtils")

package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.eco.util.ListUtils
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.aureliumskills.AureliumSkillsIntegration
import com.willfp.libreforge.integrations.ecoskills.EcoSkillsIntegration
import com.willfp.libreforge.triggers.Triggers
import org.apache.commons.lang.StringUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*

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
        plugin.eventManager.registerListener(TridentHolderDataAttacher(plugin))
        for (condition in Conditions.values()) {
            plugin.eventManager.registerListener(condition)
        }
        for (effect in Effects.values()) {
            if (effect is Listener) {
                plugin.eventManager.registerListener(effect)
            }
        }
        for (trigger in Triggers.values()) {
            plugin.eventManager.registerListener(trigger)
        }
    }

    @JvmStatic
    fun disable(plugin: EcoPlugin) {
        for (player in Bukkit.getOnlinePlayers()) {
            try {
                for (holder in player.getHolders()) {
                    for ((effect) in holder.effects) {
                        effect.disableForPlayer(player)
                    }
                }
            } catch (e: Exception) {
                Bukkit.getLogger().warning("Error disabling effects, not important - do not report this")
            }
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

    @JvmStatic
    fun logViolation(id: String, context: String, violation: ConfigViolation) {
        plugin.logger.warning("")
        plugin.logger.warning("Invalid configuration for $id in context $context:")
        plugin.logger.warning("(Cause) Argument '${violation.param}'")
        plugin.logger.warning("(Reason) ${violation.message}")
        plugin.logger.warning("")
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
