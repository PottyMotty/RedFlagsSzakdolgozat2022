

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val koin_version: String by project
val ktorm_version: String by project


plugins {
    application
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10"
    id("io.ktor.plugin") version "2.2.1"

}


group = "sporti"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.cio.EngineMain")
}
tasks.create("stage"){
    dependsOn("installDist")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}
ktor{
    docker{
        jreVersion.set(io.ktor.plugin.features.JreVersion.JRE_11)
        localImageName.set("red-flags-image")
    }
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-websockets:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-server-sessions:$ktor_version")
    implementation("io.ktor:ktor-server-cio:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    implementation("com.google.code.gson:gson:2.8.6")

    implementation("io.insert-koin:koin-core:$koin_version")
    // Koin for Ktor
    implementation("io.insert-koin:koin-ktor:$koin_version")
    // SLF4J Logger
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    implementation("org.ktorm:ktorm-core:$ktorm_version")
    implementation("com.zaxxer:HikariCP:5.0.0")
    implementation("org.postgresql:postgresql:42.2.2")

    implementation("org.ktorm:ktorm-support-postgresql:$ktorm_version")

    //AWS

    implementation("aws.sdk.kotlin:s3:0.17.7-beta")
    implementation("aws.smithy.kotlin:http-client-engine-ktor-jvm:0.12.8")
}
