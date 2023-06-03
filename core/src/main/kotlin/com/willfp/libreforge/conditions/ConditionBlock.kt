package com.willfp.libreforge.conditions

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compiled
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.applyHolder
import com.willfp.libreforge.effects.EffectList
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * A single condition config block.
 */
class ConditionBlock<T> internal constructor(
    val condition: Condition<T>,
    override val config: Config,
    override val compileData: T,
    val notMetEffects: EffectList,
    val notMetLines: List<String>,
    val showNotMet: Boolean,
    val isInverted: Boolean
) : Compiled<T> {
    private val syncMetCache = Caffeine.newBuilder()
        .expireAfterAccess(10, TimeUnit.SECONDS)
        .build<UUID, Boolean>()

    @Deprecated(
        "Use isMet(player, holder) instead.",
        ReplaceWith("condition.isMet(player, config, compileData)"),
        DeprecationLevel.ERROR
    )
    fun isMet(player: Player): Boolean {
        return isMet(player, EmptyProvidedHolder)
    }

    fun isMet(player: Player, holder: ProvidedHolder): Boolean {
        /*

        Conditions are not thread-safe, so we must run them on the main thread.
        However, conditions being met or not needs to work on packet processing threads,
        so we cache the synchronous result and return that if we are not on the main thread.

         */

        if (!Bukkit.isPrimaryThread()) {
            /*

            Assume true if not on main thread, because most calls when not on the main thread
            will be from packet processing threads, for things like not-met-lines, which
            should default to conditions being met to avoid players being shown incorrect
            information.

             */

            return syncMetCache.getIfPresent(player.uniqueId) ?: true
        }

        val withHolder = config.applyHolder(holder, player)

        val metWith = condition.isMet(player, withHolder, holder, compileData)
        val metWithout = condition.isMet(player, withHolder, compileData)

        val isMet = (metWith && metWithout) xor isInverted

        syncMetCache.put(player.uniqueId, isMet)

        return isMet
    }
}
