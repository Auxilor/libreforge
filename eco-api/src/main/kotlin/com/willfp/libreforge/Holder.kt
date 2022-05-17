package com.willfp.libreforge

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.ConfiguredEffect
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.TimeUnit

private val lineCache = Caffeine.newBuilder()
    .expireAfterWrite(1, TimeUnit.SECONDS)
    .build<LineCacheEntry, List<String>>()

interface Holder {
    val id: String
    val effects: Set<ConfiguredEffect>
    val conditions: Set<ConfiguredCondition>

    fun getNotMetLines(player: Player): List<String> {
        return lineCache.get(LineCacheEntry(player.uniqueId, this.id)) {
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


private data class LineCacheEntry(
    val uuid: UUID,
    val holderID: String
)
