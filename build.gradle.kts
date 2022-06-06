import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

/**
 *      COMMAND FOR BUILD!!
 *      ./gradlew shadowJar
 */

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val kmongo_version: String by project
val koin_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.31"
//    id("org.jetbrains.kotlin.jvm") version "1.5.31"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.0"
    kotlin("kapt") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "ktor-server"
version = "1.0.1"
application {
//    mainClass.set("com.foggyskies.ApplicationKt")
    mainClass.set("io.ktor.server.netty.EngineMain")
//    mainClass.set("ktor-server-petaap")
    project.setProperty("mainClassName", mainClass.get())
//    val isDevelopment: Boolean = project.ext.has("development")
//    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-websockets:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-server-sessions:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

// KMongo
    implementation("org.litote.kmongo:kmongo:$kmongo_version")
    implementation("org.litote.kmongo:kmongo-coroutine:$kmongo_version")

    // Koin core features
    implementation("io.insert-koin:koin-core:$koin_version")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")


//    implementation("io.ktor:ktor-client-cio:$ktor_version")
//    implementation("io.ktor:ktor-client-serialization:$ktor_version")

    kapt("org.litote.kmongo:kmongo-annotation-processor:$kmongo_version")
}

//tasks{
//    shadowJar {
//        manifest {
//            attributes(Pair("Main-Class", "com.foggyskies.ApplicationKt"))
//        }
//    }
//}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    manifest {
        attributes(
            "Main-Class" to application.mainClass.get()
        )
    }
}