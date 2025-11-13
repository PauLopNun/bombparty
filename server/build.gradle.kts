plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
    id("com.github.johnrengelman.shadow")
}

group = "com.bombparty"
version = "1.0.0"

dependencies {
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-websockets:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    implementation("io.ktor:ktor-server-call-logging:2.3.7")
    implementation("io.ktor:ktor-server-cors:2.3.7")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    implementation("ch.qos.logback:logback-classic:1.4.11")

    testImplementation("io.ktor:ktor-server-test-host:2.3.7")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

application {
    mainClass.set("com.bombparty.server.ApplicationKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.shadowJar {
    archiveBaseName.set("server")
    archiveClassifier.set("all")
    archiveVersion.set("")
    manifest {
        attributes["Main-Class"] = "com.bombparty.server.ApplicationKt"
    }
}
