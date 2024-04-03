import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("org.springframework.boot") version "3.2.4"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    `project-report`
}

group = "me.geso"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.testcontainers:testcontainers-bom:1.19.7"))
    implementation(platform("io.ktor:ktor-bom:2.3.9"))
    implementation(platform("com.amazonaws:aws-java-sdk-bom:1.12.694"))
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.2.4"))

    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-servlet")
    implementation("io.ktor:ktor-server-html-builder")
    implementation("io.ktor:ktor-server-default-headers")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")

    implementation("com.amazonaws:aws-java-sdk-s3")

    implementation("javax.xml.bind:jaxb-api:2.3.1")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // NLP
    implementation("com.atilika.kuromoji:kuromoji-ipadic:0.9.0")

    // markdown
    implementation("com.vladsch.flexmark:flexmark-all:0.64.8")

    // mybatis, mysql
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")
    runtimeOnly("com.mysql:mysql-connector-j:8.3.0")

    implementation("io.github.microutils:kotlin-logging:4.0.0-beta-2")

    // feed generation
    implementation("com.rometools:rome:2.1.0")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus:1.12.4")

    // testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.28.0")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")
    testImplementation("io.mockk:mockk-jvm:1.13.10")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
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
            "BP_JVM_VERSION" to "17",
        )
    )
}

detekt {
    config.setFrom(files("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}

