package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.entity.Firework

object EffectFirework : Effect<List<FireworkEffect>>("firework") {
    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    @Suppress("DEPRECATION")
    override fun onTrigger(config: Config, data: TriggerData, compileData: List<FireworkEffect>): Boolean {
        val location = data.location ?: return false

        val firework = location.world.createEntity(location, Firework::class.java)

        val lifespan = if (config.getInt("lifespan") < 1) 1 else config.getInt("lifespan")
        if (Prerequisite.HAS_PAPER.isMet)
            firework.ticksToDetonate = lifespan
        else
            firework.maxLife = lifespan

        val meta = firework.fireworkMeta
        meta.addEffects(compileData)
        firework.fireworkMeta = meta

        firework.spawnAt(location)

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): List<FireworkEffect> {
        val effects = mutableListOf<FireworkEffect>()

        for (section in config.getSubsections("effects")) {
            val type = try {
                FireworkEffect.Type.valueOf(section.getFormattedString("type").uppercase())
            } catch (_: IllegalArgumentException) {
                continue
            }

            val colors = parseColors(section.getFormattedString("colors")) ?: continue
            val fadeColors =
                if (config.getFormattedString("fade_colors").equals("false", ignoreCase = true)) emptyList()
                else parseColors(config.getFormattedString("fade_colors")) ?: emptyList()

            val effect = FireworkEffect.builder()
                .with(type)
                .withColor(colors)
                .withFade(fadeColors)
                .trail(config.getBool("trail"))
                .flicker(config.getBool("flicker"))
                .build()

            effects += effect
        }

        return effects
    }

    private fun parseColor(input: String): Color? {
        if (!input.startsWith("#")) return null
        val hex = input.removePrefix("#").toIntOrNull(16) ?: return null
        return Color.fromRGB(hex)
    }

    private fun parseColors(input: String): List<Color>? {
        if (input.isBlank()) return emptyList()
        return input.split(",").mapNotNull { parseColor(it) }.takeIf { it.isNotEmpty() }
    }
}
