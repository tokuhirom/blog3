import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("org.springframework.boot") version "3.4.1"
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("com.gorylenko.gradle-git-properties") version "2.4.2"
    id("io.gitlab.arturbosch.detekt") version "1.23.7"
    `project-report`
}

group = "me.geso"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.testcontainers:testcontainers-bom:1.20.4"))
    implementation(platform("io.ktor:ktor-bom:3.0.3"))
    implementation(platform("software.amazon.awssdk:bom:2.29.37"))
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.1"))

    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-servlet")
    implementation("io.ktor:ktor-server-html-builder")
    implementation("io.ktor:ktor-server-default-headers")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")

    implementation("software.amazon.awssdk:s3")

    implementation("javax.xml.bind:jaxb-api:2.3.1")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // NLP
    implementation("com.atilika.kuromoji:kuromoji-ipadic:0.9.0")

    // markdown
    implementation("com.vladsch.flexmark:flexmark-all:0.64.8")

    // mybatis, mysql
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.4")
    runtimeOnly("com.mysql:mysql-connector-j:9.1.0")

    implementation("io.github.microutils:kotlin-logging:4.0.0-beta-2")

    // feed generation
    implementation("com.rometools:rome:2.1.0")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus:1.14.2")

    // testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.28.1")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")
    testImplementation("io.mockk:mockk-jvm:1.13.13")
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        // Make sure output from standard out or error is shown in Gradle output.
        showStandardStreams = true
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}

tasks.withType<BootBuildImage> {
    environment.set(
        mapOf(
            "BPL_JVM_THREAD_COUNT" to "30",
            "BP_JVM_VERSION" to "21",
        ),
    )
}

detekt {
    config.setFrom(files("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}
