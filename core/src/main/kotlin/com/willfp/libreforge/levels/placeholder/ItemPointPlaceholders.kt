package com.willfp.libreforge.levels.placeholder

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.libreforge.levels.LevelTypes
import com.willfp.libreforge.levels.levels
import com.willfp.libreforge.points

class ItemPointsPlaceholder(plugin: EcoPlugin) : ItemPointPlaceholder(plugin, "points") {
    override fun getValue(context: PlaceholderContext, type: String): Double? {
        val item = context.itemStack ?: return null
        return item.points[type]
    }
}

class ItemLevelPlaceholder(plugin: EcoPlugin) : ItemPointPlaceholder(plugin, "level") {
    override fun getValue(context: PlaceholderContext, type: String): Double? {
        val item = context.itemStack ?: return null
        return item.levels[LevelTypes[type]].level.toDouble()
    }
}

class ItemXPRequiredPlaceholder(plugin: EcoPlugin) : ItemPointPlaceholder(plugin, "xp_required") {
    override fun getValue(context: PlaceholderContext, type: String): Double? {
        val item = context.itemStack ?: return null
        val level = LevelTypes[type] ?: return null

        return level.getXPRequired(item.levels[level].level, context)
    }
}

class ItemXPPlaceholder(plugin: EcoPlugin) : ItemPointPlaceholder(plugin, "xp") {
    override fun getValue(context: PlaceholderContext, type: String): Double? {
        val item = context.itemStack ?: return null
        return item.levels[LevelTypes[type]].xp
    }
}

class ItemProgressPlaceholder(plugin: EcoPlugin) : ItemPointPlaceholder(plugin, "progress") {
    override fun getValue(context: PlaceholderContext, type: String): Double? {
        val item = context.itemStack ?: return null
        val level = LevelTypes[type] ?: return null

        return (item.levels[level].xp / level.getXPRequired(item.levels[level].level, context)) * 100
    }
}
