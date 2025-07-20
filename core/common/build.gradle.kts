dependencies {
    implementation("dev.romainguy:kotlin-math:1.5.3") {
        isTransitive = false
    }
    implementation("com.willfp:ModelEngineBridge:1.3.0")

    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-text-minimessage:4.14.0")
    compileOnly("com.github.NuVotifier:NuVotifier:2.7.2")
    compileOnly("dev.aurelium:auraskills-api-bukkit:2.0.0")
    compileOnly("com.github.Archy-X:AureliumSkills:Beta1.2.4")
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.1.202") {
        exclude(group = "com.sk89q.worldedit", module = "worldedit-core")
        exclude(group = "com.sk89q.worldedit", module = "worldedit-legacy")
    }
    compileOnly("com.github.Zrips:Jobs:v4.17.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.1.8")
    compileOnly("com.github.brcdev-minecraft:shopgui-api:2.2.0")
    compileOnly("com.github.Gypopo:EconomyShopGUI-API:1.1.0")
    compileOnly("com.github.N0RSKA:ScytherAPI:55a")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7")
    compileOnly("net.william278.husktowns:husktowns-bukkit:3.0.8")
    compileOnly("net.william278.huskclaims:huskclaims-bukkit:1.5.2")
    compileOnly("net.momirealms:custom-crops:3.6.29")
    compileOnly("net.momirealms:custom-fishing:2.3.8")
    compileOnly("net.citizensnpcs:citizens-main:2.0.31-SNAPSHOT") {
        exclude(group = "net.byteflux", module = "libby-bukkit")
    }
    compileOnly("nl.chimpgamer.ultimatemobcoins:paper:1.8.1")
    compileOnly("com.artillexstudios:AxTrade:1.14.0")
    compileOnly("com.github.angeschossen:LandsAPI:7.15.19")

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
