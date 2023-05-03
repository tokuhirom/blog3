import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("org.springframework.boot") version "2.7.7"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.spring") version "1.8.21"
    kotlin("plugin.serialization") version "1.8.21"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
}

group = "me.geso"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:1.18.0")
        mavenBom("io.ktor:ktor-bom:2.3.0")
        mavenBom("com.amazonaws:aws-java-sdk-bom:1.12.462")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-servlet")
    implementation("io.ktor:ktor-server-html-builder")
    implementation("io.ktor:ktor-server-default-headers")
    implementation("io.ktor:ktor-server-call-logging")

    implementation("com.amazonaws:aws-java-sdk-s3")

    implementation("io.kweb:kweb-core:1.4.1")

    implementation("javax.xml.bind:jaxb-api:2.3.1")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.github.ben-manes.caffeine:caffeine:3.1.6")

    // NLP
    implementation("com.atilika.kuromoji:kuromoji-ipadic:0.9.0")

    // markdown
    implementation("com.vladsch.flexmark:flexmark-all:0.64.4")

    // mybatis, mysql
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.1")
    runtimeOnly("mysql:mysql-connector-java")

    implementation("io.github.microutils:kotlin-logging:3.0.5")

    // feed generation
    implementation("com.rometools:rome:2.1.0")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")
    testImplementation("io.mockk:mockk-jvm:1.13.5")
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
    environment = mapOf(
        "BPL_JVM_THREAD_COUNT" to "30",
        "BP_JVM_VERSION" to "17",
    )
}

detekt {
    config = files("config/detekt/detekt.yml")
    buildUponDefaultConfig = true
}

