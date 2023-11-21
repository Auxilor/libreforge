package com.willfp.libreforge.conditions

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compiled
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.applyHolder
import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.EffectList
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.Dispatcher
import com.willfp.libreforge.triggers.PlayerDispatcher
import com.willfp.libreforge.triggers.get
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
    val notMetEffects: Chain?,
    val notMetLines: List<String>,
    forceShowNotMet: Boolean,
    val isInverted: Boolean
) : Compiled<T> {
    /**
     * If the condition should show anything if not met.
     */
    val showNotMet = forceShowNotMet || notMetLines.isNotEmpty()

    private val syncMetCache = Caffeine.newBuilder()
        .expireAfterAccess(10, TimeUnit.SECONDS)
        .build<UUID, Boolean>()

    /**
     * Check if the condition is met for a [dispatcher].
     */
    fun isMet(dispatcher: Dispatcher<*>, holder: ProvidedHolder): Boolean {
        /*

        Conditions are not thread-safe, so we must run them on the main thread.
        However, conditions being met or not needs to work on packet processing threads,
        so we cache the synchronous result and return that if we are not on the main thread.

         */

        if (!Bukkit.isPrimaryThread()) {
            /*
            If the value isn't cached, then submit a task to cache it to avoid desync.
             */

            if (!syncMetCache.asMap().containsKey(dispatcher.uuid)) {
                plugin.scheduler.run {
                    // Double check that it isn't cached by the time we run
                    if (!syncMetCache.asMap().containsKey(dispatcher.uuid)) {
                        syncMetCache.put(dispatcher.uuid, isMet(dispatcher, holder))
                    }
                }
            }

            return syncMetCache.getIfPresent(dispatcher.uuid)
                ?: plugin.configYml.getBool("conditions.default-state-off-main-thread")
        }

        val withHolder = config.applyHolder(holder, dispatcher)

        val dispatcherMet = condition.isMet(dispatcher, withHolder, holder, compileData)

        // Support for legacy conditions
        @Suppress("DEPRECATION")
        val metWith = dispatcher.get<Player>()?.let { condition.isMet(it, withHolder, holder, compileData) } ?: true
        @Suppress("DEPRECATION")
        val metWithout = dispatcher.get<Player>()?.let { condition.isMet(it, withHolder, compileData) } ?: true

        val isMet = (metWith && metWithout && dispatcherMet) xor isInverted

        syncMetCache.put(dispatcher.uuid, isMet)

        return isMet
    }

    @Deprecated(
        "Use isMet(dispatcher, holder) instead",
        ReplaceWith("isMet(dispatcher, holder)"),
        DeprecationLevel.ERROR
    )
    fun isMet(player: Player, holder: ProvidedHolder): Boolean =
        isMet(PlayerDispatcher(player), holder)
}
