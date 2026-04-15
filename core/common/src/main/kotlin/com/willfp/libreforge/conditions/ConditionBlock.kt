package com.willfp.libreforge.conditions

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compiled
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.applyHolder
import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.plugin
import org.bukkit.Bukkit
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
        .maximumSize(1000)
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
            return syncMetCache.getIfPresent(dispatcher.uuid)
                ?: plugin.configYml.getBool("conditions.default-state-off-main-thread")
        }

        val withHolder = config.applyHolder(holder, dispatcher)

        val dispatcherMet = condition.isMet(dispatcher, withHolder, holder, compileData)

        val isMet = dispatcherMet xor isInverted

        syncMetCache.put(dispatcher.uuid, isMet)

        return isMet
    }
}