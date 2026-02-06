dependencies {
    implementation("dev.romainguy:kotlin-math:1.6.0") {
        isTransitive = false
    }
    implementation("com.willfp:ModelEngineBridge:1.3.0")

    compileOnly("org.purpurmc.purpur:purpur-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-text-minimessage:4.26.1")
    compileOnly("com.github.NuVotifier:NuVotifier:2.7.2")
    compileOnly("dev.aurelium:auraskills-api-bukkit:2.3.3")
    compileOnly("com.github.Archy-X:AureliumSkills:Beta1.2.4")
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.2.049") {
        exclude(group = "com.sk89q.worldedit", module = "worldedit-core")
        exclude(group = "com.sk89q.worldedit", module = "worldedit-legacy")
    }
    compileOnly("com.github.Zrips:Jobs:v5.2.6.3") {
        exclude(group = "com.bgsoftware", module = "WildStackerAPI")
    }
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude("*", "*")
    }
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.2.3")
    compileOnly("com.github.brcdev-minecraft:shopgui-api:3.2.0") {
        exclude("*", "*")
    }
    compileOnly("com.github.Gypopo:EconomyShopGUI-API:1.9.0")
    compileOnly("com.github.N0RSKA:ScytherAPI:55a")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.15")
    compileOnly("net.william278.husktowns:husktowns-bukkit:3.1.4")
    compileOnly("net.william278.huskclaims:huskclaims-bukkit:1.5.10")
    compileOnly("net.momirealms:custom-crops:3.6.29")
    compileOnly("net.momirealms:custom-fishing:2.3.20")
    compileOnly("io.lumine:Mythic:5.11.1")
    compileOnly("io.lumine:LumineUtils:1.21-SNAPSHOT")
    compileOnly("net.citizensnpcs:citizens-main:2.0.41-SNAPSHOT") {
        exclude(group = "net.byteflux", module = "libby-bukkit")
    }
    compileOnly("nl.chimpgamer.ultimatemobcoins:paper:2.0.1")
    // For mobcoins
    compileOnly("com.github.shynixn.mccoroutine:mccoroutine-folia-api:2.22.0")
    compileOnly("com.artillexstudios:AxTrade:1.21.1")
    compileOnly("com.artillexstudios.axenvoy:AxEnvoy:2.2.1")
    compileOnly("com.github.angeschossen:LandsAPI:7.23.1")
    compileOnly("com.nexomc:nexo:1.17.0") {
        exclude(group = "*", module = "*")
    }

    compileOnly(fileTree("../../lib") {
        include("*.jar")
    })
}

repositories {
    maven("https://jitpack.io/")
}

configurations.all {
    exclude(group = "com.sk89q.worldedit", module = "worldedit-core")
}
