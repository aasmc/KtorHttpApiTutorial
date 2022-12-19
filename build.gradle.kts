val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project
val h2_version: String by project
val hikari_version: String by project
val ehcache_version: String by project
val commons_codec_version: String by project
val hibernate_version: String by project
val javax_persistence_version: String by project
val hibernate_validator_veraion: String by project


plugins {
    application
    kotlin("jvm") version "1.7.20"
    id("io.ktor.plugin") version "2.1.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.20"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.7.20"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.7.20"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.7.20"
}


allOpen {
    annotations("jakarta.persistence.Entity", "jakarta.persistence.MappedSuperclass", "jakarta.persistence.Embedabble")
}

noArg {
    annotations("jakarta.persistence.Entity", "jakarta.persistence.MappedSuperclass", "jakarta.persistence.Embedabble")
}

group = "aasmc.ru"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
    implementation("commons-codec:commons-codec:$commons_codec_version")

    implementation("org.ehcache:ehcache:$ehcache_version")

    implementation("org.hibernate:hibernate-core:$hibernate_version")
    implementation("org.hibernate:hibernate-envers:$hibernate_version")
    implementation("org.hibernate.orm:hibernate-hikaricp:$hibernate_version")
    implementation ("com.zaxxer:HikariCP:$hikari_version")

    // validation
    implementation("org.hibernate.validator:hibernate-validator:$hibernate_validator_veraion")
    // need this to use Jakarta validation
    implementation("org.glassfish.expressly:expressly:5.0.0")

    // Utils
    implementation("org.apache.commons:commons-lang3:3.0")
    // https://mvnrepository.com/artifact/jakarta.platform/jakarta.jakartaee-api
    compileOnly("jakarta.platform:jakarta.jakartaee-api:8.0.0")
    // https://mvnrepository.com/artifact/org.jboss.arquillian.extension/arquillian-persistence-impl
    implementation("org.jboss.arquillian.extension:arquillian-persistence-impl:1.0.0.Alpha6")



    implementation("com.h2database:h2:$h2_version")

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven:3.1.4")
    testImplementation("org.jboss.arquillian.testng:arquillian-testng-container:1.6.0.Final")
    testImplementation("org.jboss.arquillian.protocol:arquillian-protocol-servlet:1.6.0.Final")
    testImplementation("org.wildfly.arquillian:wildfly-arquillian-container-remote:3.0.1.Final")
    testImplementation("org.jboss.arquillian.extension:arquillian-rest-client-api:1.0.0.Alpha4")
    // https://mvnrepository.com/artifact/org.jboss.arquillian.extension/arquillian-rest-client-impl-3x
    testImplementation("org.jboss.arquillian.extension:arquillian-rest-client-impl-3x:1.0.0.Alpha4")

}

tasks.test {
    useJUnitPlatform()
}