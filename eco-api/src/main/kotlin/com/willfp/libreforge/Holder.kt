package com.willfp.libreforge

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.ConfiguredEffect
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit

private val lineCache = Caffeine.newBuilder()
    .expireAfterWrite(1, TimeUnit.SECONDS)
    .build<Int, List<String>>()

private val anyNotMetCache = Caffeine.newBuilder()
    .expireAfterWrite(1, TimeUnit.SECONDS)
    .build<Int, Boolean>()

internal object BlankHolder : Holder {
    override val id = "internal:blank"
    override val conditions = emptyList<ConfiguredCondition>()
    override val effects = emptyList<ConfiguredEffect>()
}

interface Holder {
    val id: String
    val effects: List<ConfiguredEffect>
    val conditions: List<ConfiguredCondition>

    fun showAnyNotMet(player: Player): Boolean {
        val hash = player.uniqueId.hashCode() * 31 + id.hashCode()

        return anyNotMetCache.get(hash) {
            for (condition in this.conditions) {
                if (condition.notMetEffects.isNotEmpty() || condition.notMetLines?.isNotEmpty() == true) {
                    if (!condition.isMet(player)) {
                        return@get true
                    }
                }
            }

            false
        }
    }

    fun getNotMetLines(player: Player): List<String> {
        val hash = player.uniqueId.hashCode() * 31 + id.hashCode()

        return lineCache.get(hash) {
            val lines = mutableListOf<String>()

            for (condition in this.conditions) {
                if (!condition.isMet(player)) {
                    lines.addAll(condition.notMetLines ?: continue)
                }
            }

            lines
        }
    }
}
