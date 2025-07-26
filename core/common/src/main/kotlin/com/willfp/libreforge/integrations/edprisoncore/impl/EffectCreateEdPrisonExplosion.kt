package com.willfp.libreforge.integrations.edprisoncore.impl

import com.edwardbelt.edprison.utils.ExplosionUtils
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectCreateEdPrisonExplosion  : Effect<NoCompileData>("create_edprison_explosion") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("radius", "You must specify the explosion radius!")
        require("silent", "You must specify whether the explosion is silent!")
        require("auto_sell", "You must specify whether to auto-sell the drops!")
        require("explosion_type", "You must specify the explosion type!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val location = data.location?.block ?: return false

        val silent = config.getBool("silent")
        val autoSell = config.getBool("auto_sell")
        val explosionType = config.getString("explosion_type")
        val radius = config.getInt("radius")

        if (explosionType == "natural") {
            ExplosionUtils.createNaturalExplosion(player, location, radius, autoSell, silent)
        }
        if (explosionType == "custom") {
            ExplosionUtils.createSphereExplosion(player, location, radius, autoSell, silent)
        }
        return true
    }
}