package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.runExempted
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.entity.Firework
import org.bukkit.event.entity.EntityShootBowEvent

object EffectShootFirework : Effect<NoCompileData>("shoot_firework") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    @Suppress("DEPRECATION")
    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val velocity = data.velocity
        val fire = ((data.event as? EntityShootBowEvent)?.projectile?.fireTicks ?: 0) > 0

        player.runExempted {
            val firework = if (velocity == null || !config.getBool("inherit_velocity")) {
                player.launchProjectile(Firework::class.java)
            } else {
                player.launchProjectile(Firework::class.java, velocity)
            }

            if (config.getBool("launch-at-location") && data.location != null) {
                firework.teleportAsync(data.location)
            }

            if (fire) {
                firework.fireTicks = Int.MAX_VALUE
            }

            if (config.getBool("no_source")) {
                firework.shooter = null
            }

            val lifespan = if (config.getInt("lifespan") < 1) 1 else config.getInt("lifespan")
            if (Prerequisite.HAS_PAPER.isMet)
                firework.ticksToDetonate = lifespan
            else
                firework.maxLife = lifespan

            val meta = firework.fireworkMeta

            for (section in config.getSubsections("effects")) {
                val type = try {
                    FireworkEffect.Type.valueOf(section.getFormattedString("type"))
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

                meta.addEffect(effect)
            }

            firework.fireworkMeta = meta
        }

        return true
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