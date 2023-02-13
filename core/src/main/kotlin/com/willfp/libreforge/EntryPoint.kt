package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.libreforge.integrations.aureliumskills.AureliumSkillsIntegration
import com.willfp.libreforge.integrations.boosters.BoostersIntegration
import com.willfp.libreforge.integrations.ecoarmor.EcoArmorIntegration
import com.willfp.libreforge.integrations.ecobosses.EcoBossesIntegration
import com.willfp.libreforge.integrations.ecoenchants.EcoEnchantsIntegration
import com.willfp.libreforge.integrations.ecoitems.EcoItemsIntegration
import com.willfp.libreforge.integrations.ecojobs.EcoJobsIntegration
import com.willfp.libreforge.integrations.ecopets.EcoPetsIntegration
import com.willfp.libreforge.integrations.ecoskills.EcoSkillsIntegration
import com.willfp.libreforge.integrations.jobs.JobsIntegration
import com.willfp.libreforge.integrations.levelledmobs.LevelledMobsIntegration
import com.willfp.libreforge.integrations.mcmmo.McMMOIntegration
import com.willfp.libreforge.integrations.paper.PaperIntegration
import com.willfp.libreforge.integrations.reforges.ReforgesIntegration
import com.willfp.libreforge.integrations.scyther.ScytherIntegration
import com.willfp.libreforge.integrations.talismans.TalismansIntegration
import com.willfp.libreforge.integrations.tmmobcoins.TMMobcoinsIntegration
import com.willfp.libreforge.integrations.vault.VaultIntegration

/*



 */

internal val plugin: EcoPlugin
    get() = LibreforgeInitializer.plugins.firstOrNull() ?: throw NoLibreforgePluginPresentError(
        "No libreforge plugin found on the server! Debug: ${LibreforgeInitializer.plugins}"
    )

/**
 * List of integrations to load.
 */
private val integrations = listOf(
    IntegrationLoader("AureliumSkills") { AureliumSkillsIntegration.load() },
    IntegrationLoader("Boosters") { BoostersIntegration.load() },
    IntegrationLoader("EcoArmor") { EcoArmorIntegration.load() },
    IntegrationLoader("EcoBosses") { EcoBossesIntegration.load() },
    IntegrationLoader("EcoEnchants") { EcoEnchantsIntegration.load() },
    IntegrationLoader("EcoItems") { EcoItemsIntegration.load() },
    IntegrationLoader("EcoJobs") { EcoJobsIntegration.load() },
    IntegrationLoader("EcoPets") { EcoPetsIntegration.load() },
    IntegrationLoader("EcoSkills") { EcoSkillsIntegration.load() },
    IntegrationLoader("Jobs") { JobsIntegration.load() },
    IntegrationLoader("LevelledMobs") { LevelledMobsIntegration.load() },
    IntegrationLoader("mcMMO") { McMMOIntegration.load() },
    IntegrationLoader("Reforges") { ReforgesIntegration.load() },
    IntegrationLoader("Scyther") { ScytherIntegration.load() },
    IntegrationLoader("Talismans") { TalismansIntegration.load() },
    IntegrationLoader("TMMobcoins") { TMMobcoinsIntegration.load() },
    IntegrationLoader("Vault") { VaultIntegration.load() },
)


private class NoLibreforgePluginPresentError(
    override val message: String
) : Error(message)

object LibreforgeInitializer {
    internal val plugins = mutableListOf<EcoPlugin>()

    fun addPlugin(plugin: EcoPlugin) {
        if (plugins.isEmpty()) {
            init()
        }

        plugins += plugin

        // To prevent problems, when a libreforge plugin is disabled,
        // the core plugin is automatically switched to the next available
        // one.
        plugin.onDisable {
            plugins -= plugin
        }
    }
}

/**
 * Init tasks.
 */
private fun init() {
    for (loader in integrations) {
        loader.loadIfPresent()
    }

    if (Prerequisite.HAS_PAPER.isMet) {
        PaperIntegration.load()
    }
}
