package com.willfp.libreforge.levels

import com.willfp.eco.core.data.get
import com.willfp.eco.core.data.has
import com.willfp.eco.core.data.newPersistentDataContainer
import com.willfp.eco.core.data.set
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

private val baseLevelData = LevelData(1, 0.0)

private val key = namespacedKeyOf("libreforge", "item_levels")
private const val LEVEL_KEY = "level"
private const val XP_KEY = "xp"

class ItemLevelMap(
    private var pdc: PersistentDataContainer?,
    private val parent: PersistentDataContainer,
    private val itemStack: ItemStack
) {
    operator fun get(level: LevelType?): LevelData {
        if (level == null || pdc == null) {
            return baseLevelData
        }

        val container = pdc!!

        if (!container.has(level.id, PersistentDataType.TAG_CONTAINER)) {
            return baseLevelData
        }

        val levelContainer = container.get(level.id, PersistentDataType.TAG_CONTAINER)!!

        return LevelData(
            levelContainer.get(LEVEL_KEY, PersistentDataType.INTEGER)!!,
            levelContainer.get(XP_KEY, PersistentDataType.DOUBLE)!!
        )
    }

    operator fun set(level: LevelType, value: LevelData) {
        require(value.level >= 0) { "Level must be positive" }
        require(value.xp >= 0) { "XP must be positive" }

        if (pdc == null) {
            this.pdc = newPersistentDataContainer()
        }

        val container = pdc!!

        if (!container.has(level.id, PersistentDataType.TAG_CONTAINER)) {
            container.set(level.id, PersistentDataType.TAG_CONTAINER, newPersistentDataContainer())
        }

        val levelContainer = container.get(level.id, PersistentDataType.TAG_CONTAINER)!!

        levelContainer.set(LEVEL_KEY, PersistentDataType.INTEGER, value.level)
        levelContainer.set(XP_KEY, PersistentDataType.DOUBLE, value.xp)

        container.set(level.id, PersistentDataType.TAG_CONTAINER, levelContainer)

        parent.set(key, PersistentDataType.TAG_CONTAINER, container)
    }

    fun gainXP(level: LevelType, xp: Double, context: PlaceholderContext) {
        val currentData = this[level]

        if (xp >= 0) {
            this[level] = currentData.gainXP(level, xp, itemStack, context)
            return
        }

        var remaining = currentData.xp + xp
        var currentLevel = currentData.level
        while (remaining < 0 && currentLevel > 1) {
            currentLevel--
            remaining += level.getXPRequired(currentLevel, context)
        }
        this[level] = LevelData(maxOf(1, currentLevel), maxOf(0.0, remaining))
    }

    fun gainLevels(level: LevelType, levels: Int, context: PlaceholderContext) {
        val currentData = this[level]
        val targetLevel = maxOf(1, currentData.level + levels)

        if (levels > 0) {
            for (l in (currentData.level + 1)..targetLevel) {
                level.handleLevelUp(l, itemStack, context)
            }
        }

        this[level] = LevelData(targetLevel, 0.0)
    }
}

val ItemStack.levels: ItemLevelMap
    get() {
        val fast = this.fast()
        return ItemLevelMap(
            fast.levelsPDC,
            fast.persistentDataContainer,
            this
        )
    }

private val FastItemStack.levelsPDC: PersistentDataContainer?
    get() {
        val pdc = this.persistentDataContainer
        if (!pdc.has(key, PersistentDataType.TAG_CONTAINER)) {
            return null
        }

        return pdc.get(key, PersistentDataType.TAG_CONTAINER)!!
    }
