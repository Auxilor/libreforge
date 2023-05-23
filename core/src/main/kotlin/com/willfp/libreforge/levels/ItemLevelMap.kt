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
    private val pdc: PersistentDataContainer,
    private val parent: PersistentDataContainer,
    private val itemStack: ItemStack
) {
    operator fun get(level: LevelType?): LevelData {
        if (level == null) {
            return baseLevelData
        }

        if (!pdc.has(level.id, PersistentDataType.TAG_CONTAINER)) {
            return baseLevelData
        }

        val levelContainer = pdc.get(level.id, PersistentDataType.TAG_CONTAINER)!!

        return LevelData(
            levelContainer.get(LEVEL_KEY, PersistentDataType.INTEGER)!!,
            levelContainer.get(XP_KEY, PersistentDataType.DOUBLE)!!
        )
    }

    operator fun set(level: LevelType, value: LevelData) {
        require(value.level >= 0) { "Level must be positive" }
        require(value.xp >= 0) { "XP must be positive" }

        if (!pdc.has(level.id, PersistentDataType.TAG_CONTAINER)) {
            pdc.set(level.id, PersistentDataType.TAG_CONTAINER, newPersistentDataContainer())
        }

        val levelContainer = pdc.get(level.id, PersistentDataType.TAG_CONTAINER)!!

        levelContainer.set(LEVEL_KEY, PersistentDataType.INTEGER, value.level)
        levelContainer.set(XP_KEY, PersistentDataType.DOUBLE, value.xp)

        pdc.set(level.id, PersistentDataType.TAG_CONTAINER, levelContainer)
        parent.set(key, PersistentDataType.TAG_CONTAINER, pdc)
    }

    fun gainXP(level: LevelType, xp: Double, context: PlaceholderContext) {
        require(xp >= 0) { "XP must be positive" }

        val currentLevel = this[level]
        val newLevel = currentLevel.gainXP(level, xp, itemStack, context)
        this[level] = newLevel
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

private val FastItemStack.levelsPDC: PersistentDataContainer
    get() {
        val pdc = this.persistentDataContainer
        if (!pdc.has(key, PersistentDataType.TAG_CONTAINER)) {
            pdc.set(key, PersistentDataType.TAG_CONTAINER, newPersistentDataContainer())
        }
        return pdc.get(key, PersistentDataType.TAG_CONTAINER)!!
    }
