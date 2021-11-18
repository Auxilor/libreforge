package com.willfp.libreforge.api.provider

import com.willfp.eco.util.ListUtils
import com.willfp.libreforge.api.LibReforge
import org.bukkit.entity.Player
import java.util.UUID
import java.util.WeakHashMap


object LibReforgeProviders {
    private val holderProviders = mutableSetOf<HolderProvider>()
    private val previousStates: MutableMap<UUID, Iterable<Holder>> = WeakHashMap()
    private val holderCache = mutableMapOf<UUID, Iterable<Holder>>()
    private val plugin = LibReforge.instance.plugin

    /**
     * Get all Holders active for the player.
     *
     * @return An iterable collection of holders.
     */
    fun providerHolders(player: Player): Iterable<Holder> {
        if (holderCache.containsKey(player.uniqueId)) {
            return holderCache[player.uniqueId]?.toList() ?: emptyList()
        }

        val holders = mutableListOf<Holder>()
        for (provider in holderProviders) {
            holders.addAll(provider.providerHolders(player))
        }

        holderCache[player.uniqueId] = holders
        plugin.scheduler.runLater({
            holderCache.remove(player.uniqueId)
        }, 40)

        return holders
    }

    /**
     * Clear caches.
     *
     * @param player The player.
     */
    fun clearCaches(player: Player) {
        holderCache.remove(player.uniqueId)
    }

    fun updateEffects(player: Player) {
        val before = mutableListOf<Holder>()
        if (previousStates.containsKey(player.uniqueId)) {
            before.addAll(previousStates[player.uniqueId] ?: emptyList())
        }
        clearCaches(player)

        plugin.scheduler.run {
            val after = providerHolders(player)
            previousStates[player.uniqueId] = after

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
                    if (!condition.isConditionMet(player, config)) {
                        areConditionsMet = false
                        break
                    }
                }

                if (areConditionsMet) {
                    for ((effect, config) in holder.effects) {
                        effect.enableForPlayer(player, config)
                    }
                }
            }
            for (holder in removed) {
                for ((effect, _) in holder.effects) {
                    effect.disableForPlayer(player)
                }
            }

            for (holder in after) {
                var areConditionsMet = true
                for ((condition, config) in holder.conditions) {
                    if (!condition.isConditionMet(player, config)) {
                        areConditionsMet = false
                        break
                    }
                }
                if (!areConditionsMet) {
                    for ((effect, _) in holder.effects) {
                        effect.disableForPlayer(player)
                    }
                }
            }
        }
    }
}