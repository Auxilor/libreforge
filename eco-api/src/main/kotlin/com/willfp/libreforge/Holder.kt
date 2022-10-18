package com.willfp.libreforge

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.ConfiguredEffect
import org.bukkit.entity.Player
import java.io.StringReader
import java.util.concurrent.TimeUnit

private val lineCache = Caffeine.newBuilder()
    .expireAfterWrite(1, TimeUnit.SECONDS)
    .build<Int, List<String>>()

internal object BlankHolder : Holder {
    override val id = "internal:blank"
    override val conditions = emptySet<ConfiguredCondition>()
    override val effects = emptySet<ConfiguredEffect>()
}

interface Holder {
    val id: String
    val effects: Set<ConfiguredEffect>
    val conditions: Set<ConfiguredCondition>

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
