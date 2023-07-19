dependencies {
    implementation("dev.romainguy:kotlin-math:1.5.3") {
        isTransitive = false
    }
    implementation("net.kyori:adventure-text-minimessage:4.14.0")

    compileOnly("com.willfp:eco:6.63.0")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")

    compileOnly("com.github.Archy-X:AureliumSkills:Beta1.2.4")
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.1.202") {
        exclude(group = "com.sk89q.worldedit", module = "worldedit-core")
        exclude(group = "com.sk89q.worldedit", module = "worldedit-legacy")
    }
    compileOnly("com.github.Zrips:Jobs:v4.17.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.0.5")
    compileOnly("com.github.brcdev-minecraft:shopgui-api:2.2.0")
    compileOnly("com.github.Gypopo:EconomyShopGUI-API:1.1.0")
    compileOnly("com.github.N0RSKA:ScytherAPI:55a")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7")

    compileOnly(fileTree("../lib") {
        include("*.jar")
    })
}

repositories {
    maven("https://jitpack.io/")
}

configurations.all {
    exclude(group = "com.sk89q.worldedit", module = "worldedit-core")
}

group = "com.willfp"
version = rootProject.version

tasks {
    shadowJar {
        relocate("dev.romainguy.kotlin.math", "com.willfp.libreforge.libs.math")
        relocate("org.apache.maven", "com.willfp.eco.libs.maven")
        relocate("net.kyori.adventure.text.minimessage", "com.willfp.libreforge.libs.minimessage")
    }

    build {
        dependsOn("publishToMavenLocal")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            artifactId = "libreforge"
            groupId = "com.willfp"

            artifact(tasks.shadowJar.get().archiveFile.get()) {
                classifier = "shadow"
            }
        }
    }
}
