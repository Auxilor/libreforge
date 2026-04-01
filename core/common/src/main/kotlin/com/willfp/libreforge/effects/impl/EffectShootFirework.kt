package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.runExempted
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.entity.Firework
import org.bukkit.event.entity.EntityShootBowEvent

object EffectShootFirework : Effect<List<FireworkEffect>>("shoot_firework") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    @Suppress("DEPRECATION")
    override fun onTrigger(config: Config, data: TriggerData, compileData: List<FireworkEffect>): Boolean {
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

            val power = if (config.getInt("power") !in 0..255) 0 else config.getInt("power")

            val meta = firework.fireworkMeta
            meta.addEffects(compileData)
            meta.power = power
            firework.fireworkMeta = meta
        }

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

            val colors = config.getStrings("colors").mapNotNull { Color.fromRGB(it.removePrefix("#").toInt(16)) }
            val fadeColors = config.getStrings("colors").mapNotNull { Color.fromRGB(it.removePrefix("#").toInt(16)) }

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
}