package com.willfp.libreforge.integrations.edprisoncore.impl

import com.edwardbelt.edprison.modules.pouches.PouchUtils
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectGiveEdPrisonPouch : Effect<NoCompileData>("give_edprison_pouch") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("type", "You must specify the type of pouch!")
        require("unlocked", "You must specify whether the pouch is unlocked!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val pouchName = config.getString("type")

        if (!PouchUtils.existPouch(pouchName)) {
            return false
        }

        val unlocked = config.getBool("unlocked")
        val blocks = if (unlocked) PouchUtils.getBlocksReqPouch(pouchName) else 0.0

        val pouchItem = PouchUtils.getPouch(player, pouchName, blocks)
        player.inventory.addItem(pouchItem)
        return true
    }
}
