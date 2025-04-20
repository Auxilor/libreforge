import org.jetbrains.kotlin.gradle.dsl.JvmTarget

dependencies {
    compileOnly(project(":core:common"))

    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.release = 21
    }

    compileKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}
