package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.entity.Firework

object EffectFirework : Effect<List<FireworkEffect>>("firework") {
    override val description = "Launches a firework at the trigger location with configurable colors, shape, and effects."
    override val categories = setOf("visual")

    override val arguments = arguments {
        optional(
            "power",
            description = "The flight duration of the firework (0–255). Defaults to 0.",
            type = ArgType.INT,
            default = "0"
        )
        optional(
            "effects",
            description = "A list of firework effect subsections, each with type, colors, fade-colors, trail, and flicker.",
            type = ArgType.DYNAMIC,
            schema = FireworkEffectSpec::class
        )
    }

    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    @Suppress("DEPRECATION")
    override fun onTrigger(config: Config, data: TriggerData, compileData: List<FireworkEffect>): Boolean {
        val location = data.location ?: return false
        val world = location.world ?: return false

        val firework = world.createEntity(location, Firework::class.java)

        val power = if (config.getInt("power") !in 0..255) 0 else config.getInt("power")

        val meta = firework.fireworkMeta
        meta.addEffects(compileData)
        meta.power = power
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

            val colors = section.getStrings("colors").mapNotNull { Color.fromRGB(it.removePrefix("#").toInt(16)) }
            val fadeColors = section.getStrings("fade-colors").mapNotNull { Color.fromRGB(it.removePrefix("#").toInt(16)) }

            val effect = FireworkEffect.builder()
                .with(type)
                .withColor(colors)
                .withFade(fadeColors)
                .trail(section.getBool("trail"))
                .flicker(section.getBool("flicker"))
                .build()

            effects += effect
        }

        return effects
    }
}
