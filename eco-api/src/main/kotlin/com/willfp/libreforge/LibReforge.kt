@file:JvmName("LibReforgeUtils")

package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.eco.util.ListUtils
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.conditions.MovementConditionListener
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.aureliumskills.AureliumSkillsIntegration
import com.willfp.libreforge.integrations.ecoskills.EcoSkillsIntegration
import com.willfp.libreforge.integrations.jobs.JobsIntegration
import com.willfp.libreforge.integrations.mcmmo.McMMOIntegration
import com.willfp.libreforge.integrations.paper.PaperIntegration
import com.willfp.libreforge.integrations.vault.VaultIntegration
import com.willfp.libreforge.triggers.Triggers
import org.apache.commons.lang.StringUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.Tameable
import java.util.UUID
import java.util.WeakHashMap

private val holderProviders = mutableSetOf<HolderProvider>()
private val previousStates: MutableMap<UUID, Iterable<Holder>> = WeakHashMap()
private val holderCache = mutableMapOf<UUID, Iterable<Holder>>()

typealias HolderProvider = (Player) -> Iterable<Holder>

abstract class LibReforgePlugin(
    resourceId: Int,
    bstatsId: Int,
    color: String,
    proxyPackage: String = ""
) : EcoPlugin(resourceId, bstatsId, proxyPackage, color, true) {
    private val defaultPackage = StringUtils.join(
        arrayOf("com", "willfp", "libreforge"),
        "."
    )

    init {
        setInstance()

        if (this.javaClass.packageName == defaultPackage) {
            throw IllegalStateException("You must shade and relocate libreforge into your jar!")
        }

        if (Prerequisite.HAS_PAPER.isMet) {
            PaperIntegration.load()
        }
    }

    open fun handleEnableAdditional() {

    }

    open fun handleDisableAdditional() {

    }

    open fun handleReloadAdditional() {

    }

    open fun loadAdditionalIntegrations(): List<IntegrationLoader> {
        return emptyList()
    }

    fun registerHolderProvider(provider: HolderProvider) {
        holderProviders.add(provider)
    }

    fun registerJavaHolderProvider(provider: java.util.function.Function<Player, Iterable<Holder>>) {
        holderProviders.add(provider::apply)
    }

    fun logViolation(id: String, context: String, violation: ConfigViolation) {
        this.logger.warning("")
        this.logger.warning("Invalid configuration for $id in context $context:")
        this.logger.warning("(Cause) Argument '${violation.param}'")
        this.logger.warning("(Reason) ${violation.message}")
        this.logger.warning("")
    }

    final override fun handleEnable() {
        this.eventManager.registerListener(TridentHolderDataAttacher(this))
        this.eventManager.registerListener(MovementConditionListener())
        for (condition in Conditions.values()) {
            this.eventManager.registerListener(condition)
        }
        for (effect in Effects.values()) {
            this.eventManager.registerListener(effect)
        }
        for (trigger in Triggers.values()) {
            this.eventManager.registerListener(trigger)
        }

        handleEnableAdditional()
    }

    final override fun handleDisable() {
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

        handleDisableAdditional()
    }

    final override fun handleReload() {
        this.scheduler.runTimer({
            for (player in Bukkit.getOnlinePlayers()) {
                player.updateEffects()
            }
        }, 30, 30)

        handleReloadAdditional()
    }

    final override fun loadIntegrationLoaders(): List<IntegrationLoader> {
        val integrations = mutableListOf(
            IntegrationLoader("EcoSkills", EcoSkillsIntegration::load),
            IntegrationLoader("AureliumSkills", AureliumSkillsIntegration::load),
            IntegrationLoader("mcMMO", McMMOIntegration::load),
            IntegrationLoader("Jobs", JobsIntegration::load),
            IntegrationLoader("Vault", VaultIntegration::load),
        )

        integrations.addAll(loadAdditionalIntegrations())

        return integrations
    }

    private fun setInstance() {
        instance = this
    }

    companion object {
        @JvmStatic
        internal lateinit var instance: LibReforgePlugin
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
        holders.addAll(provider(this))
    }

    holderCache[this.uniqueId] = holders
    LibReforgePlugin.instance.scheduler.runLater({
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

fun Entity.tryAsPlayer(): Player? {
    return when (this) {
        is Projectile -> this.shooter as? Player
        is Player -> this
        is Tameable -> this.owner as? Player
        else -> null
    }
}
