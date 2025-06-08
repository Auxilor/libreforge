package com.willfp.libreforge.conditions.impl


import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.LivingEntity

object ConditionHasPotionEffect : Condition<NoCompileData>("has_potion_effect") {
    override val arguments = arguments {
        require(listOf("effect", "effects"), "You must specify the potion effect!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val livingEntity = dispatcher.get<LivingEntity>() ?: return false

        val targetEffects = buildList {
            config.getString("effect")?.let { add(it.lowercase()) }
            addAll(config.getStrings("effects").map { it.lowercase() })
        }

        if (targetEffects.isEmpty()) return false

        val requiredLevel = config.getInt("level")  // Will return 0 if not set, but we check with `config.has`

        return livingEntity.activePotionEffects.any { effect ->
            val matchesEffect = effect.type.key.key.lowercase() in targetEffects
            val matchesLevel = !config.has("level") || (effect.amplifier + 1) >= requiredLevel

            matchesEffect && matchesLevel
        }
    }
}