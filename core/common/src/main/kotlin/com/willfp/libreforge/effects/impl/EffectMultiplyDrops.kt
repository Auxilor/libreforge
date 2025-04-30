package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.matches
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.DropResult
import com.willfp.libreforge.triggers.event.EditableDropEvent
import org.bukkit.Bukkit
import org.bukkit.block.data.Ageable
import kotlin.math.roundToInt

object EffectMultiplyDrops : Effect<NoCompileData>("multiply_drops") {
    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override val arguments = arguments {
        require(listOf("multiplier", "fortune"), "You must specify a multiplier or level of fortune to mimic!")
        //require("only_fully_grown", "You must specify if only fully grown crops should be replanted!")
    }

    private val whitelist = mutableListOf<TestableItem>()

    override fun postRegister() {
        plugin.onReload {
            whitelist.clear()
            whitelist.addAll(plugin.configYml.getStrings("effects.multiply_drops.whitelist").map { Items.lookup(it) })
        }
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? EditableDropEvent ?: return false


        val isBlacklisting = plugin.configYml.getBool("effects.multiply_drops.prevent-duplication")
        val whitelist = plugin.configYml.getStrings("effects.multiply_drops.whitelist").map { Items.lookup(it) }

        var multiplier = if (config.has("fortune")) {
            val fortune = config.getIntFromExpression("fortune", data)
            (Math.random() * (fortune.toDouble() - 1) + 1.1).roundToInt()
        } else if (config.has("multiplier")) {
            config.getDoubleFromExpression("multiplier", data).roundToInt()
        } else 1



        //----only fully grown crops-------//
        //TODO add config check
        //if(){
        //
        //}

        if(data.blockData!=null){
            val blockData = data.blockData
            if (blockData is Ageable && blockData.age != blockData.maximumAge) {
                multiplier = 1
            }
        }
        //----only fully grown crops-------//

        event.addModifier {
            var matches = true
            if (config.has("on_items")) {
                val items = config.getStrings("on_items").map { string -> Items.lookup(string) }
                matches = items.any { test -> test.matches(it) }
            }

            if (it.maxStackSize > 1 && matches) {
                if (it.type.isOccluding && isBlacklisting && !whitelist.matches(it)) {
                    return@addModifier DropResult(it, 0)
                }

                it.amount *= multiplier
            }

            DropResult(it, 0)
        }

        return true
    }
}
