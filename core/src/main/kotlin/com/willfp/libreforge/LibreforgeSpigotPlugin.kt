package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.libreforge.configs.ChainsYml
import com.willfp.libreforge.configs.lrcdb.CommandLrcdb
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.executors.impl.NormalExecutorFactory
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
import org.bukkit.Bukkit
import org.bukkit.event.Listener

internal lateinit var plugin: EcoPlugin
    private set

class LibreforgeSpigotPlugin : EcoPlugin() {
    val chainsYml = ChainsYml(this)

    init {
        plugin = this
    }

    override fun handleEnable() {
        this.logger.info("")
        this.logger.info("Hey, what's this plugin doing here? I didn't install it!")
        this.logger.info("libreforge is the effects system for plugins like EcoEnchants,")
        this.logger.info("EcoJobs, EcoItems, etc. If you're looking for config options for")
        this.logger.info("things like cooldown messages, lrcdb, and stuff like that, you'll")
        this.logger.info("find it under /plugins/libreforge")
        this.logger.info("")
        this.logger.info("Don't worry about updating libreforge, it's handled automatically!")
        this.logger.info("")

        if (Prerequisite.HAS_PAPER.isMet) {
            PaperIntegration.load(this)
        }

        pointsPlaceholder(this).register()
    }

    override fun handleReload() {
        for (config in chainsYml.getSubsections("chains")) {
            Effects.register(
                config.getString("id"),
                Effects.compileChain(
                    config.getSubsections("effects"),
                    NormalExecutorFactory.create(),
                    ViolationContext(this, "chains.yml")
                ) ?: continue
            )
        }

        // Poll for changes
        this.scheduler.runTimer(20, 20) {
            for (player in Bukkit.getOnlinePlayers()) {
                player.refreshHolders()
            }
        }
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            TriggerPlaceholderListener,
            EffectCollisionFixer,
            ItemRefreshListener(this)
        )
    }

    override fun loadIntegrationLoaders(): List<IntegrationLoader> {
        return listOf(
            IntegrationLoader("AureliumSkills") { AureliumSkillsIntegration.load(this) },
            IntegrationLoader("Boosters") { BoostersIntegration.load(this) },
            IntegrationLoader("EcoArmor") { EcoArmorIntegration.load(this) },
            IntegrationLoader("EcoBosses") { EcoBossesIntegration.load(this) },
            IntegrationLoader("EcoEnchants") { EcoEnchantsIntegration.load(this) },
            IntegrationLoader("EcoItems") { EcoItemsIntegration.load(this) },
            IntegrationLoader("EcoJobs") { EcoJobsIntegration.load(this) },
            IntegrationLoader("EcoPets") { EcoPetsIntegration.load(this) },
            IntegrationLoader("EcoSkills") { EcoSkillsIntegration.load(this) },
            IntegrationLoader("Jobs") { JobsIntegration.load(this) },
            IntegrationLoader("LevelledMobs") { LevelledMobsIntegration.load(this) },
            IntegrationLoader("mcMMO") { McMMOIntegration.load(this) },
            IntegrationLoader("Reforges") { ReforgesIntegration.load(this) },
            IntegrationLoader("Scyther") { ScytherIntegration.load(this) },
            IntegrationLoader("Talismans") { TalismansIntegration.load(this) },
            IntegrationLoader("TMMobcoins") { TMMobcoinsIntegration.load(this) },
            IntegrationLoader("Vault") { VaultIntegration.load(this) }
        )
    }

    override fun loadPluginCommands(): List<PluginCommand> {
        return listOf(
            CommandLrcdb(this)
        )
    }
}
