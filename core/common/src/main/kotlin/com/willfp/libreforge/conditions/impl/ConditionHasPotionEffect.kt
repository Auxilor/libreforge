package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.LivingEntity

object ConditionHasPotionEffect : Condition<List<String>>("has_potion_effect") {
    override val arguments = arguments {
        require(listOf("effect", "effects"), "You must specify the potion effect!")
    }

    override fun makeCompileData(config: Config, context: ViolationContext): List<String> {
        return buildList {
            config.getStringOrNull("effect")?.let { add(it.lowercase()) }
            addAll(config.getStrings("effects").map { it.lowercase() })
        }
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: List<String>
    ): Boolean {
        val livingEntity = dispatcher.get<LivingEntity>() ?: return false

        if (compileData.isEmpty()) return false

        val requiredLevel = config.getInt("level")  // Will return 0 if not set, but we check with `config.has`

        return livingEntity.activePotionEffects.any { effect ->
            val matchesEffect = effect.type.key.key.lowercase() in compileData
            val matchesLevel = !config.has("level") || (effect.amplifier + 1) >= requiredLevel

            matchesEffect && matchesLevel
        }
    }
}
